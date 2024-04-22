package com.yanyan.service;

import com.yanyan.domain.School;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yanyan.dto.Result;

/**
* @author 韶光善良君
* @description 针对表【yy_school(院校信息)】的数据库操作Service
* @createDate 2024-04-15 12:33:48
*/
public interface SchoolService extends IService<School> {

    Result queryAllCoursesList(Integer current);

    Result querySchoolById(Long id);

    void saveSchools2Redis(Long expireSeconds) throws InterruptedException;
}
