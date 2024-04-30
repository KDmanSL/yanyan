package com.yanyan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanyan.domain.PostReply;
import com.yanyan.dto.PostReplyDTO;
import com.yanyan.service.PostReplyService;
import com.yanyan.mapper.PostReplyMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 韶光善良君
* @description 针对表【yy_post_reply(帖子回复表)】的数据库操作Service实现
* @createDate 2024-04-05 17:23:53
*/
@Service
public class PostReplyServiceImpl extends ServiceImpl<PostReplyMapper, PostReply>
    implements PostReplyService{
    @Resource
    PostReplyMapper postReplyMapper;

    public List<PostReplyDTO> queryPostReplyWithUserInfoByPostId(Long postId) {
        return postReplyMapper.selectPostReplyWithUserInfo(postId);
    }
}




