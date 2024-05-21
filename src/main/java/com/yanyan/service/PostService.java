package com.yanyan.service;

import com.yanyan.domain.Post;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yanyan.dto.AddPostDTO;
import com.yanyan.dto.Result;

/**
* @author 韶光善良君
* @description 针对表【yy_post(论坛帖子)】的数据库操作Service
* @createDate 2024-04-05 17:23:50
*/
public interface PostService extends IService<Post> {

    Result queryAllPostList(Integer current);

    void savePost2Redis(Long expireSeconds) throws InterruptedException;

    Result queryPostListByUserId(Long userId, Integer current);

    Result addPost(AddPostDTO postDTO);

    Result deletePost(Long postId);

    Result addLike(Long postId);
}
