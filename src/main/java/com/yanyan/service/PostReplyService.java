package com.yanyan.service;

import com.yanyan.domain.PostReply;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yanyan.dto.PostReplyDTO;

import java.util.List;

/**
* @author 韶光善良君
* @description 针对表【yy_post_reply(帖子回复表)】的数据库操作Service
* @createDate 2024-04-05 17:23:53
*/
public interface PostReplyService extends IService<PostReply> {
    public List<PostReplyDTO> queryPostReplyWithUserInfoByPostId(Long postId);
}
