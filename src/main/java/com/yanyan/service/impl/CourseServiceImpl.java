package com.yanyan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanyan.domain.Course;
import com.yanyan.dto.Result;
import com.yanyan.service.CourseService;
import com.yanyan.mapper.CourseMapper;
import org.springframework.stereotype.Service;

/**
* @author 韶光善良君
* @description 针对表【yy_course(课程表)】的数据库操作Service实现
* @createDate 2024-04-05 17:15:26
*/
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course>
    implements CourseService{

    @Override
    public Result queryCourseById(Long id) {
        Course course = this.getById(id);
        return Result.ok(course);
    }
}




