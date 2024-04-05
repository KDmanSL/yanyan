package com.yanyan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanyan.domain.UserFavorites;
import com.yanyan.service.UserFavoritesService;
import com.yanyan.mapper.UserFavoritesMapper;
import org.springframework.stereotype.Service;

/**
* @author 韶光善良君
* @description 针对表【yy_user_favorites(用户收藏的课程)】的数据库操作Service实现
* @createDate 2024-04-05 17:24:13
*/
@Service
public class UserFavoritesServiceImpl extends ServiceImpl<UserFavoritesMapper, UserFavorites>
    implements UserFavoritesService{

}




