package com.yanyan.mapper;

import com.yanyan.domain.Course;
import com.yanyan.domain.UserFavorites;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author 韶光善良君
* @description 针对表【yy_user_favorites(用户收藏的课程)】的数据库操作Mapper
* @createDate 2024-04-05 17:24:13
* @Entity com.yanyan.domain.UserFavorites
*/
public interface UserFavoritesMapper extends BaseMapper<UserFavorites> {
    List<Course> selectCourseInfoByUserId(Long userId);

}




