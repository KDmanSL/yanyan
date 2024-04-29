package com.yanyan.service;

import com.yanyan.domain.UserFavorites;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yanyan.dto.Result;
import jakarta.servlet.http.HttpSession;

/**
* @author 韶光善良君
* @description 针对表【yy_user_favorites(用户收藏的课程)】的数据库操作Service
* @createDate 2024-04-05 17:24:13
*/
public interface UserFavoritesService extends IService<UserFavorites> {

    Result queryUserFavoritesByUserId(Integer current, Integer size);

    Result addUserFavorites(Long courseId);
}
