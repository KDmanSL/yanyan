package com.yanyan.controller;

import com.yanyan.dto.Result;
import com.yanyan.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/course")
@RestController
public class CourseController {
    @Autowired
    CourseService courseService;

    @GetMapping(value = "/{id}")
    public Result queryCourseById(@PathVariable("id") Long id){
        return courseService.queryCourseById(id);
    }
}
