package com.yanyan.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanyan.domain.Course;
import com.yanyan.dto.Result;
import com.yanyan.service.CourseService;
import com.yanyan.mapper.CourseMapper;
import com.yanyan.utils.RedisConstants;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import static com.yanyan.utils.RedisConstants.*;
import java.util.Comparator;
import java.util.List;
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
    public Result queryAllCoursesList() {
        //TODO 修改为带有页面参数的分页查询 要使用Redis缓存
//        //1.从redis查询商铺列表缓存
//        List<String> listCache=stringRedisTemplate.opsForList().range(COURSE_ALL_LIST_KEY, 0, -1);
//        //2.判断是否为空
//        if (listCache != null && !listCache.isEmpty()) {
//            //不为空，返回列表
//            List<Course> coursesList = listCache.stream()
//                    .map(str -> (Course) JSONUtil.toBean(str, Course.class, true))
//                    .collect(Collectors.toList());
//            return Result.ok(coursesList);
//        }
        //为空，从数据库查询
        List<Course> courseList = list();
//        if(courseList==null){
//            return Result.fail("查询类型列表失败");
//        }
//        List<String> strList = courseList.stream().map(JSONUtil::toJsonStr).collect(Collectors.toList());
//        stringRedisTemplate.opsForList().leftPushAll(RedisConstants.COURSE_ALL_LIST_KEY,strList);
//        stringRedisTemplate.expire(COURSE_ALL_LIST_KEY, COURSE_ALL_LIST_TTL, TimeUnit.MINUTES);
        return Result.ok(courseList);
    }

    // TODO 添加根据专业查找课程的功能
    @Override
    public Result queryCoursesListByMajorId(Long majorId) {
        return null;
    }

}




