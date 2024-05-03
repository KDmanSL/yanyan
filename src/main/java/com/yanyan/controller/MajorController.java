package com.yanyan.controller;

import com.yanyan.domain.Major;
import com.yanyan.dto.Result;
import com.yanyan.service.MajorService;
import com.yanyan.service.SchoolMajorService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping(value = "/major")
@CrossOrigin
@RestController
public class MajorController {
    @Resource
    private MajorService majorService;
    @Resource
    private SchoolMajorService schoolMajorService;

    /**
     * 查询专业列表
     * @return 专业列表
     * @throws InterruptedException 线程中断异常
     */
    @GetMapping(value = "/list")
    public Result queryAllMajorsList() throws InterruptedException {
        return majorService.queryAllMajorsList();
    }

    /**
     * 根据学校名称查询专业列表
     * @param schoolName 学校名称
     *
     * @return 专业列表
     */
    @GetMapping(value = "/{schoolName}")
    public Result queryMajorsListBySchoolName(@PathVariable("schoolName") String schoolName) {
        List<Major> majorList = schoolMajorService.queryMajorNameBySchoolName(schoolName);
        if(majorList.isEmpty()){
            return Result.fail("未查询到专业信息");
        }
        return Result.ok(majorList);
    }

}
