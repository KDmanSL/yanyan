package com.yanyan.controller;

import com.yanyan.domain.School;
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
     * @param is211 是否211
     * @param current 当前页
     * @param size    每页大小
     * @return 学校信息
     */
    @GetMapping(value = "/list")
    public Result queryAllSchoolList(
            @RequestParam(value = "is211", required = false) Integer is211,
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {

        return schoolService.queryAllSchoolList(is211, current, size);
    }

    /**
     * 根据id查询学校信息
     *
     * @param id 学校id
     * @return 学校信息
     */
    @GetMapping(value = "/{id}")
    public Result querySchoolById(@PathVariable("id") Long id) {
        School school = schoolService.querySchoolById(id);
        if (school == null) {
            return Result.fail("未找到对应的院校信息");
        }
        return Result.ok(school);
    }

    /**
     * 根据地区查询学校信息
     *
     * @param is211 是否211
     * @param area 地区
     * @param current 当前页
     * @param size    每页大小
     * @return 学校信息
     */
    @GetMapping(value = "/area/{area}")
    public Result querySchoolByArea(
            @RequestParam(value = "is211", required = false) Integer is211,
            @PathVariable("area") String area,
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return schoolService.querySchoolByArea(is211, area, current, size);
    }

    /**
     * 根据学校名称查询学校
     *
     * @param name 学校名称
     * @return 学校信息
     */
    @GetMapping(value = "/name/{name}")
    public Result querySchoolByName(@PathVariable("name") String name,
                                    @RequestParam(value = "current", defaultValue = "1") Integer current,
                                    @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return schoolService.querySchoolListByName(name, current, size);
    }


}
