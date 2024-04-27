package com.yanyan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanyan.domain.Post;
import com.yanyan.dto.PostDTO;
import com.yanyan.dto.Result;
import com.yanyan.service.PostService;
import com.yanyan.mapper.PostMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

/**
* @author 韶光善良君
* @description 针对表【yy_post(论坛帖子)】的数据库操作Service实现
* @createDate 2024-04-05 17:23:50
*/
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post>
    implements PostService{
    // TODO 查询所有帖子，查询某个用户的所有帖子，添加帖子信息和发送者
    @Override
    public Result queryAllPostList() {
        return null;
    }

    @Override
    public Result queryPostListByUserId(Long userId) {
        return null;
    }

    @Override
    public Result addPost(PostDTO postDTO) {
        return null;
    }
}




