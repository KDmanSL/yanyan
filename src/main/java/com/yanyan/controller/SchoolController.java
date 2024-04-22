package com.yanyan.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanyan.domain.School;
import com.yanyan.dto.Result;
import com.yanyan.service.SchoolService;
import com.yanyan.utils.SystemConstants;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value ="/school")
@CrossOrigin
public class SchoolController {
    @Resource
    SchoolService schoolService;

    /**
     * 查询全部学校列表
     * @return 学校列表
     */
    @GetMapping(value = "/list")
    public Result queryAllSchoolList(@RequestParam(value = "current",defaultValue = "1")Integer current){

        return Result.ok(schoolService.queryAllCoursesList(current));
    }

    /**
     * 根据id查询学校信息
     * @param id 学校id
     * @return 学校信息
     */
    @GetMapping(value = "/{id}")
    public Result querySchoolById(@PathVariable("id") Long id){
        return schoolService.querySchoolById(id);
    }


}
