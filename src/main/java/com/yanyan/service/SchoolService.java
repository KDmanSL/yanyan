package com.yanyan.service;

import com.yanyan.domain.School;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yanyan.dto.Result;

import java.util.List;

/**
* @author 韶光善良君
* @description 针对表【yy_school(院校信息)】的数据库操作Service
* @createDate 2024-04-15 12:33:48
*/
public interface SchoolService extends IService<School> {

    Result queryAllSchoolList(Integer is211, Integer current, Integer size);

    School querySchoolById(Long id);

    void saveSchools2Redis(Long expireSeconds) throws InterruptedException;

    Result querySchoolByArea(Integer is211, String area, Integer current, Integer size);

    School querySchoolByName(String name);

    Result querySchoolListByName(String name,Integer current, Integer size);
}
