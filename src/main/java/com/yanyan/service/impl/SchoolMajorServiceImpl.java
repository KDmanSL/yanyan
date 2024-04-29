package com.yanyan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanyan.domain.Major;
import com.yanyan.domain.School;
import com.yanyan.domain.SchoolMajor;
import com.yanyan.dto.Result;
import com.yanyan.service.MajorService;
import com.yanyan.service.SchoolMajorService;
import com.yanyan.mapper.SchoolMajorMapper;
import com.yanyan.service.SchoolService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @author 韶光善良君
* @description 针对表【yy_school_major(院校专业)】的数据库操作Service实现
* @createDate 2024-04-05 17:24:01
*/
@Service
public class SchoolMajorServiceImpl extends ServiceImpl<SchoolMajorMapper, SchoolMajor>
    implements SchoolMajorService{
    @Resource
    private SchoolService schoolService;
    @Resource
    private MajorService majorService;
    @Override
    public List<Major> queryMajorNameBySchoolName(String schoolName) {
         School school= schoolService.querySchoolByName(schoolName);
         if(school == null){
             return null;
         }
        Long schoolId = school.getId();
        // 获取schoolId下的所有专业ID
        List<SchoolMajor> schoolMajorList = query().eq("schoolId", schoolId).list();
        if (schoolMajorList == null){
            return null;
        }
        List<Major> majorList = new ArrayList<>();
        List<Long> majorIdList = schoolMajorList.stream().map(SchoolMajor::getMajorid).toList();
        for (Long majorId : majorIdList){
            Major major = majorService.queryMajorById(majorId);
            majorList.add(major);
        }
        return majorList;
    }
}




