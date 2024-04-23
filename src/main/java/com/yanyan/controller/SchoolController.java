package com.yanyan.controller;

import com.yanyan.dto.Result;
import com.yanyan.service.SchoolService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/school")
@CrossOrigin
public class SchoolController {
    @Resource
    SchoolService schoolService;

    /**
     * 查询所有学校信息
     *
     * @param current 当前页
     * @param size    每页大小
     * @return 学校信息
     */
    @GetMapping(value = "/list")
    public Result queryAllSchoolList(@RequestParam(value = "current", defaultValue = "1") Integer current,
                                     @RequestParam(value = "size", defaultValue = "10") Integer size) {

        return schoolService.queryAllCoursesList(current, size);
    }

    /**
     * 根据id查询学校信息
     *
     * @param id 学校id
     * @return 学校信息
     */
    @GetMapping(value = "/{id}")
    public Result querySchoolById(@PathVariable("id") Long id) {
        return schoolService.querySchoolById(id);
    }

    /**
     * 根据地区查询学校信息
     *
     * @param area 地区
     * @param current 当前页
     * @param size    每页大小
     * @return 学校信息
     */
    @GetMapping(value = "/area/{area}")
    public Result querySchoolByArea(@PathVariable("area") String area,
                                    @RequestParam(value = "current", defaultValue = "1") Integer current,
                                    @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return schoolService.querySchoolByArea(area, current, size);
    }

}
