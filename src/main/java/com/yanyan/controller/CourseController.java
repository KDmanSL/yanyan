package com.yanyan.controller;

import com.yanyan.dto.Result;
import com.yanyan.service.CourseService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping(value = "/course")
@CrossOrigin
@RestController
public class CourseController {
    @Resource
    CourseService courseService;

    /**
     * 根据id查询课程
     * @param id 课程id
     * @return 课程信息
     */
    @GetMapping(value = "/{id}")
    public Result queryCourseById(@PathVariable("id") Long id){
        return courseService.queryCourseById(id);
    }
}
