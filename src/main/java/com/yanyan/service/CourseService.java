package com.yanyan.service;

import com.yanyan.domain.Course;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yanyan.dto.Result;

/**
* @author 韶光善良君
* @description 针对表【yy_course(课程表)】的数据库操作Service
* @createDate 2024-04-05 17:15:26
*/
public interface CourseService extends IService<Course> {
    Result queryCourseById(Long id);

}
