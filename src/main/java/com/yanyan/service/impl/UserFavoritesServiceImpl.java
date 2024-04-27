package com.yanyan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanyan.domain.UserFavorites;
import com.yanyan.dto.Result;
import com.yanyan.service.UserFavoritesService;
import com.yanyan.mapper.UserFavoritesMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

/**
* @author 韶光善良君
* @description 针对表【yy_user_favorites(用户收藏的课程)】的数据库操作Service实现
* @createDate 2024-04-05 17:24:13
*/
//TODO 根据用户id查询收藏的课程，通过用户id添加收藏课程id
@Service
public class UserFavoritesServiceImpl extends ServiceImpl<UserFavoritesMapper, UserFavorites>
    implements UserFavoritesService{
    @Override
    public Result queryUserFavoritesByUserId(HttpSession session, Integer current, Integer size) {
        return null;
    }

    @Override
    public Result addUserFavorites(HttpSession session, Long courseId) {
        return null;
    }



}




