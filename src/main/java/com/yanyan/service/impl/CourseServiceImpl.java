package com.yanyan.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanyan.domain.Course;
import com.yanyan.domain.MajorCourse;
import com.yanyan.dto.CourseDetailDTO;
import com.yanyan.dto.MajorCourseDTO;
import com.yanyan.dto.Result;
import com.yanyan.mapper.MajorCourseMapper;
import com.yanyan.service.CourseService;
import com.yanyan.mapper.CourseMapper;
import com.yanyan.service.MajorService;
import com.yanyan.service.UserFavoritesService;
import com.yanyan.utils.UserHolder;
import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static com.yanyan.utils.RedisConstants.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author 韶光善良君
 * @description 针对表【yy_course(课程表)】的数据库操作Service实现
 * @createDate 2024-04-05 17:15:26
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course>
        implements CourseService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private MajorCourseMapper majorCourseMapper;

    @Resource
    private UserFavoritesService userFavoritesService;

    @Override
    public Result queryCourseById(Long id) {
        //1.从redis查询商铺列表缓存
        List<String> listCache = stringRedisTemplate.opsForList().range(COURSE_ALL_LIST_KEY, 0, -1);
        //2.判断是否为空
        if (listCache != null && !listCache.isEmpty()) {
            //不为空，返回列表
            List<MajorCourseDTO> coursesList = listCache.stream()
                    .map(str -> (MajorCourseDTO) JSONUtil.toBean(str, MajorCourseDTO.class, true))
                    .filter(course -> Objects.equals(course.getCourseId(), id))
                    .collect(Collectors.toList());
            CourseDetailDTO courseDetailDTO = new CourseDetailDTO();
            if (coursesList.isEmpty()) {
                return Result.fail("未找到id为" + id + "的内容");
            }
            courseDetailDTO.setMajorCourseDTO(coursesList.get(0));
            // 检查是否收藏
            Long courseId = coursesList.get(0).getCourseId();
            courseDetailDTO.setIsFavorite(userFavoritesService.isFavorite(courseId));
            return Result.ok(courseDetailDTO);
        }
        // 缓存为空，则重建缓存
        saveCourses2Redis(COURSE_ALL_LIST_TTL);

        return queryCourseById(id);
    }


    @Override
    public Result queryAllCoursesList(Integer current, Integer size) {

        int start = (current - 1) * size;
        int end = current * size - 1;

        //1.从redis查询商铺列表缓存
        List<String> listCache = stringRedisTemplate.opsForList().range(COURSE_ALL_LIST_KEY, 0, -1);
        //2.判断是否为空
        if (listCache != null && !listCache.isEmpty()) {
            //不为空，返回列表
            List<MajorCourseDTO> coursesList = listCache.stream()
                    .map(str -> (MajorCourseDTO) JSONUtil.toBean(str, MajorCourseDTO.class, true))
                    .distinct() // 去除同样课程不同专业的重复课程
                    .collect(Collectors.toList());
            Long totalPage = (long) Math.ceil((double) coursesList.size() / size);
            // 检查分页索引，防止越界
            int listSize = coursesList.size();
            start = Math.max(start, 0); // 确保开始索引不是负数
            end = Math.min(end, listSize - 1); // 确保结束索引不超出列表大小

            // 当开始索引超过列表大小时，返回空列表
            if (start >= listSize) {
                return Result.fail("超出页面请求范围");
            }

            // 安全地进行分页
            List<MajorCourseDTO> nowPageList = coursesList.subList(start, end + 1);
            return Result.ok(nowPageList, totalPage);
        }

        // 缓存为空，则重建缓存
        saveCourses2Redis(COURSE_ALL_LIST_TTL);
        return queryAllCoursesList(current, size);
    }
    @Resource
    private RedissonClient redissonClient;

    /**
     * 缓存预热
     *
     * @param expireSeconds 过期时间
     * @throws InterruptedException 线程中断异常
     */
    @Override
    public void saveCourses2Redis(Long expireSeconds) {

        // 这个地方改成存list而不是哈希，
        // 然后我封装了一个自定义sql同时存对应的课程分类成的major信息，
        // 之后查对应的course也是从对应的list中查找
        // 检查数据库缓存是否存在，存在则删除缓存

        // 添加分布式锁
        RLock lock = redissonClient.getLock(CACHE_COURSE_LOCK_KEY);
        boolean isLock = lock.tryLock();
        if (isLock) {
            try {
                if (stringRedisTemplate.hasKey(COURSE_ALL_LIST_KEY)) {
                    stringRedisTemplate.delete(COURSE_ALL_LIST_KEY);
                }

                List<MajorCourseDTO> courseList = majorCourseMapper.selectCourseMajorWithDetails();

                List<String> strList = courseList.stream().map(JSONUtil::toJsonStr).collect(Collectors.toList());
                stringRedisTemplate.opsForList().rightPushAll(COURSE_ALL_LIST_KEY, strList);
                stringRedisTemplate.expire(COURSE_ALL_LIST_KEY, expireSeconds, TimeUnit.MINUTES);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                //释放锁
                lock.unlock();
            }
        }
    }

    // 带分页的查询（课程页面分类查询使用）
    @Override
    public Result queryCoursesListByMajorIdWithPages(Long majorId, Integer current, Integer size) {
        int start = (current - 1) * size;
        int end = current * size - 1;

        //1.从redis查询商铺列表缓存
        List<String> listCache = stringRedisTemplate.opsForList().range(COURSE_ALL_LIST_KEY, 0, -1);
        //2.判断是否为空
        if (listCache != null && !listCache.isEmpty()) {
            //不为空，返回列表
            List<MajorCourseDTO> coursesList = listCache.stream()
                    .map(str -> (MajorCourseDTO) JSONUtil.toBean(str, MajorCourseDTO.class, true))
                    .filter(majorCourseDTO -> majorCourseDTO.getMajorId().equals(majorId))
                    .sorted(Comparator.comparingLong(MajorCourseDTO::getCourseId))
                    .collect(Collectors.toList());
            Long totalPage = (long) Math.ceil((double) coursesList.size() / size);
            // 检查分页索引，防止越界
            int listSize = coursesList.size();
            start = Math.max(start, 0); // 确保开始索引不是负数
            end = Math.min(end, listSize - 1); // 确保结束索引不超出列表大小

            // 当开始索引超过列表大小时，返回空列表
            if (start >= listSize) {
                return Result.fail("超出页面请求范围");
            }

            // 安全地进行分页
            List<MajorCourseDTO> nowPageList = coursesList.subList(start, end + 1);

            List<CourseDetailDTO> courseDetailList = new ArrayList<>();
            for (MajorCourseDTO majorCourseDTO : nowPageList) {
                CourseDetailDTO courseDetailDTO = new CourseDetailDTO();
                courseDetailDTO.setMajorCourseDTO(majorCourseDTO);
                courseDetailDTO.setIsFavorite(userFavoritesService.isFavorite(majorCourseDTO.getCourseId()));
                courseDetailList.add(courseDetailDTO);
            }


            return Result.ok(courseDetailList, totalPage);
        }

        // 缓存为空，则重建缓存
        saveCourses2Redis(COURSE_ALL_LIST_TTL);
        return queryCoursesListByMajorIdWithPages(majorId, current, size);
    }

    // 不带分页的查询 （主页课程推荐使用）
    @Override
    public Result queryCoursesListByMajorId(Long majorId) {
        //1.从redis查询商铺列表缓存
        List<String> listCache = stringRedisTemplate.opsForList().range(COURSE_ALL_LIST_KEY, 0, -1);
        //2.判断是否为空
        if (listCache != null && !listCache.isEmpty()) {
            //不为空，返回列表
            List<MajorCourseDTO> coursesList = listCache.stream()
                    .map(str -> (MajorCourseDTO) JSONUtil.toBean(str, MajorCourseDTO.class, true))
                    .filter(majorCourseDTO -> majorCourseDTO.getMajorId().equals(majorId))
                    .sorted(Comparator.comparingLong(MajorCourseDTO::getCourseId))
                    .collect(Collectors.toList());
            return Result.ok(coursesList);
        }
        // 缓存为空，则重建缓存
        saveCourses2Redis(COURSE_ALL_LIST_TTL);
        return queryCoursesListByMajorId(majorId);
    }

    @Override
    public Result queryCourseListByName(String name, Integer current, Integer size) {
        int start = (current - 1) * size;
        int end = current * size - 1;
        List<String> listCache = stringRedisTemplate.opsForList().range(COURSE_ALL_LIST_KEY, 0, -1);
        if (listCache != null && !listCache.isEmpty()) {
            //不为空，返回列表
            List<MajorCourseDTO> coursesList = listCache.stream()
                    .map(str -> (MajorCourseDTO) JSONUtil.toBean(str, MajorCourseDTO.class, true))
                    .filter(courseDTO -> courseDTO.getCourseName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());

            if (coursesList.isEmpty()) {
                return Result.fail("未找到课程信息");
            }
            Long totalPage = (long) Math.ceil((double) coursesList.size() / size);

            start = Math.max(start, 0);
            end = Math.min(end, coursesList.size() - 1);
            if (start >= coursesList.size()) {
                return Result.fail("超出页面请求范围");
            }
            // 返回当前页数据
            List<MajorCourseDTO> nowPageList = coursesList.subList(start, end + 1);

            return Result.ok(nowPageList, totalPage);
        }
        // 缓存为空，则重建缓存
        saveCourses2Redis(COURSE_ALL_LIST_TTL);
        return queryCourseListByName(name, current, size);
    }

    @Override
    public Result deleteCourse(Long courseId) {
        // 检查权限
        String userRole = UserHolder.getUser().getRole();
        if(userRole.equals("stu")){
            return Result.fail("无权限删除课程");
        }

        Course course = this.getById(courseId);
        if (course == null) {
            return Result.fail("课程不存在");
        }

        removeById(courseId);
        saveCourses2Redis(COURSE_ALL_LIST_TTL);
        return Result.ok("课程删除成功");
    }
    @Resource
    private MajorService majorService;
    @Resource
    private Snowflake snowflake;

    @Override
    public Result addCourse(MajorCourseDTO courseDTO) {
        // 检查权限
        String userRole = UserHolder.getUser().getRole();
        if(userRole.equals("stu")){
            return Result.fail("无权限删除课程");
        }

        if (courseDTO == null) {
            return Result.fail("课程信息不能为空");
        }
        if (courseDTO.getCourseName() == null || courseDTO.getCourseDescription() == null || courseDTO.getCourseUrl() == null || courseDTO.getCourseImgUrl() == null) {
            return Result.fail("课程信息不能为空");
        }
        if (courseDTO.getMajorId() == null || courseDTO.getMajorName() == null) {
            return Result.fail("课程相关专业不能为空");
        }
        if(majorService.queryMajorById(courseDTO.getMajorId())==null){
            return Result.fail("专业不存在");
        }
        Long courseId =snowflake.nextId();
        Course course = new Course();
        course.setId(courseId);
        course.setName(courseDTO.getCourseName());
        course.setDescription(courseDTO.getCourseDescription());
        course.setUrl(courseDTO.getCourseUrl());
        course.setImgUrl(courseDTO.getCourseImgUrl());
        this.save(course);

        MajorCourse majorCourse = new MajorCourse();
        majorCourse.setMajorid(courseDTO.getMajorId());
        majorCourse.setCourseid(courseId);
        majorCourseMapper.insert(majorCourse);

        saveCourses2Redis(COURSE_ALL_LIST_TTL);

        return Result.ok("课程添加成功");

    }
}




