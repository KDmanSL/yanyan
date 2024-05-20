package com.yanyan.service;

import com.yanyan.domain.Course;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yanyan.dto.MajorCourseDTO;
import com.yanyan.dto.Result;

/**
* @author 韶光善良君
* @description 针对表【yy_course(课程表)】的数据库操作Service
* @createDate 2024-04-05 17:15:26
*/
public interface CourseService extends IService<Course> {
    Result queryCourseById(Long id);
    Result queryAllCoursesList(Integer current, Integer size);

    /**
     * 缓存预热
     *
     * @param expireSeconds 过期时间
     * @throws InterruptedException 线程中断异常
     */
    void saveCourses2Redis(Long expireSeconds) throws InterruptedException;

    // 带分页的查询（课程页面分类查询使用）
    Result queryCoursesListByMajorIdWithPages(Long majorId, Integer current, Integer size);

    // 不带分页的查询 （主页课程推荐使用）
    Result queryCoursesListByMajorId(Long majorId);

    Result queryCourseListByName(String name, Integer current, Integer size);

    Result deleteCourse(Long courseId);

    Result addCourse(MajorCourseDTO courseDTO);
}
