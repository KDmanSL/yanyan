package com.yanyan.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanyan.domain.Course;
import com.yanyan.dto.Result;
import com.yanyan.service.CourseService;
import com.yanyan.mapper.CourseMapper;
import com.yanyan.utils.RedisConstants;
import com.yanyan.utils.RedisData;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import static com.yanyan.utils.RedisConstants.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
* @author 韶光善良君
* @description 针对表【yy_course(课程表)】的数据库操作Service实现
* @createDate 2024-04-05 17:15:26
*/
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course>
    implements CourseService{
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryCourseById(Long id) {
        // TODO 添加redis
        Course course = this.getById(id);
        return Result.ok(course);
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
            List<Course> coursesList = listCache.stream()
                    .map(str -> (Course) JSONUtil.toBean(str, Course.class, true))
                    .collect(Collectors.toList());
            // 检查分页索引，防止越界
            int listSize = coursesList.size();
            start = Math.max(start, 0); // 确保开始索引不是负数
            end = Math.min(end, listSize - 1); // 确保结束索引不超出列表大小

            // 当开始索引超过列表大小时，返回空列表
            if (start >= listSize) {
                return Result.fail("超出页面请求范围");
            }

            // 安全地进行分页
            List<Course> nowPageList = coursesList.subList(start, end + 1);
            return Result.ok(nowPageList);
        }
        //3.缓存为空，查询数据库
        List<Course> coursesList = query().list();
        if (coursesList==null) {
            return Result.fail("查询课程列表失败");
        }
        

        // 将数据写入redis
        List<String> strList = coursesList.stream().map(JSONUtil::toJsonStr).collect(Collectors.toList());
        stringRedisTemplate.opsForList().rightPushAll(RedisConstants.COURSE_ALL_LIST_KEY, strList);
        stringRedisTemplate.expire(COURSE_ALL_LIST_KEY, COURSE_ALL_LIST_TTL, TimeUnit.MINUTES);

        Long totalPage = (long) Math.ceil((double) coursesList.size() / size);

        start = Math.max(start, 0);
        end = Math.min(end, coursesList.size() - 1);
        if (start >= coursesList.size()) {
            return Result.fail("超出页面请求范围");
        }
        // 返回当前页数据
        List<Course> nowPageList = coursesList.subList(start, end + 1);


        return Result.ok(nowPageList,totalPage);

    }


    /**
     * 缓存预热
     *
     * @param expireSeconds 过期时间
     * @throws InterruptedException 线程中断异常
     */
    @Override
    public void saveCourses2Redis(Long expireSeconds) throws InterruptedException {
        List<Course> schools = list();
        for (Course course : schools) {
            RedisData redisData = new RedisData();
            // 添加过期时间，随机添加1-10的随机数，防止雪崩
            Random random = new Random();
            redisData.setExpireTime(LocalDateTime.now().plusMinutes(expireSeconds).plusSeconds(random.nextInt(10)));
            redisData.setData(course);
            stringRedisTemplate.opsForValue().set(CACHE_SCHOOL_KEY + course.getId(), JSONUtil.toJsonStr(redisData));
        }
    }

    // TODO 添加根据专业查找课程的功能
    @Override
    public Result queryCoursesListByMajorId(Long majorId) {
        return null;
    }

}




