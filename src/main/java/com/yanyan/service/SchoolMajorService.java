package com.yanyan.service;

import com.yanyan.domain.SchoolMajor;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yanyan.dto.Result;

/**
* @author 韶光善良君
* @description 针对表【yy_school_major(院校专业)】的数据库操作Service
* @createDate 2024-04-05 17:24:01
*/
public interface SchoolMajorService extends IService<SchoolMajor> {
    Result queryMajorNameBySchoolName(String schoolName);
}
