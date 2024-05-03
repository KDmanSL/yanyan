package com.yanyan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanyan.domain.Course;
import com.yanyan.domain.UserFavorites;
import com.yanyan.dto.Result;
import com.yanyan.service.UserFavoritesService;
import com.yanyan.mapper.UserFavoritesMapper;
import com.yanyan.utils.UserHolder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 韶光善良君
* @description 针对表【yy_user_favorites(用户收藏的课程)】的数据库操作Service实现
* @createDate 2024-04-05 17:24:13
*/
@Service
public class UserFavoritesServiceImpl extends ServiceImpl<UserFavoritesMapper, UserFavorites>
    implements UserFavoritesService{
    @Override
    public Result queryUserFavoritesByUserId(Integer current, Integer size) {

        int start = (current - 1) * size;
        int end = current * size - 1;

        Long userId = UserHolder.getUser().getId();
        List<Course> courses = this.baseMapper.selectCourseInfoByUserId(userId);
        if (courses.isEmpty()) {
            return Result.fail("暂无收藏课程");
        }
        // 检查分页索引，防止越界
        int listSize = courses.size();
        start = Math.max(start, 0); // 确保开始索引不是负数
        end = Math.min(end, listSize - 1); // 确保结束索引不超出列表大小

        // 当开始索引超过列表大小时，返回空列表
        if (start >= listSize) {
            return Result.fail("超出页面请求范围");
        }

        // 安全地进行分页
        List<Course> nowPageList = courses.subList(start, end + 1);
        return Result.ok(nowPageList);
    }

    // 不是添加收藏的课程，而是包含有取消收藏的状态转换  如果已收藏则取消收藏，如果未收藏则收藏
    @Override
    public Result addUserFavorites(Long courseId) {

        Long userId = UserHolder.getUser().getId();

        UserFavorites userFavorites = query().eq("userId", userId).eq("courseId", courseId).one();

        if (userFavorites == null) {
            // 未收藏，添加收藏
            UserFavorites userFavorites1 = new UserFavorites();
            userFavorites1.setUserid(userId);
            userFavorites1.setCourseid(courseId);
            save(userFavorites1);
            return Result.ok("收藏成功");
        } else {
            // 已收藏，取消收藏
            removeById(userFavorites.getId());
            return Result.ok("取消收藏成功");
        }
    }
    @Override
    public Boolean isFavorite(Long courseId) {
        Long userId = UserHolder.getUser().getId();
        UserFavorites userFavorites = query().eq("userId", userId).eq("courseId", courseId).one();
        return userFavorites != null;
    }
}




