package com.yanyan.mapper;

import com.yanyan.domain.MajorCourse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanyan.dto.MajorCourseDTO;

import java.util.List;

/**
* @author 韶光善良君
* @description 针对表【yy_major_course(考目标专业需要学习的课程)】的数据库操作Mapper
* @createDate 2024-04-05 17:23:47
* @Entity com.yanyan.domain.MajorCourse
*/
public interface MajorCourseMapper extends BaseMapper<MajorCourse> {
    List<MajorCourseDTO> selectCourseMajorWithDetails();
}




