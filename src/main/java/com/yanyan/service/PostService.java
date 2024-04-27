package com.yanyan.service;

import com.yanyan.domain.Post;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yanyan.dto.PostDTO;
import com.yanyan.dto.Result;
import jakarta.servlet.http.HttpSession;

/**
* @author 韶光善良君
* @description 针对表【yy_post(论坛帖子)】的数据库操作Service
* @createDate 2024-04-05 17:23:50
*/
public interface PostService extends IService<Post> {

    Result queryAllPostList();
    Result queryPostListByUserId(Long userId);

    Result addPost(PostDTO postDTO);
}
