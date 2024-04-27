package com.yanyan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanyan.domain.SchoolMajor;
import com.yanyan.dto.Result;
import com.yanyan.service.SchoolMajorService;
import com.yanyan.mapper.SchoolMajorMapper;
import org.springframework.stereotype.Service;

/**
* @author 韶光善良君
* @description 针对表【yy_school_major(院校专业)】的数据库操作Service实现
* @createDate 2024-04-05 17:24:01
*/
@Service
public class SchoolMajorServiceImpl extends ServiceImpl<SchoolMajorMapper, SchoolMajor>
    implements SchoolMajorService{
    // TODO 通过学校查询该学校开设的专业

    @Override
    public Result queryMajorNameBySchoolName(String schoolName) {
        return null;
    }
}




