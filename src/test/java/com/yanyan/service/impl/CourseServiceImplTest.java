package com.yanyan.service.impl;

import com.yanyan.dto.Result;
import com.yanyan.service.CourseService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
class CourseServiceImplTest {

    @Resource
    private CourseService courseService;

    @BeforeEach
    void setUp() {

    }

    @Test
    void queryAllCoursesList() {
        Result result = courseService.queryAllCoursesList(1, 8);
        System.out.println(result);
    }

    @Test
    void saveCourses2Redis() {
        try {
            courseService.saveCourses2Redis(100000L);
            System.out.println("success!");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}