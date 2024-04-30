package com.yanyan.mapper;

import com.yanyan.domain.PostReply;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanyan.dto.PostReplyDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 韶光善良君
* @description 针对表【yy_post_reply(帖子回复表)】的数据库操作Mapper
* @createDate 2024-04-05 17:23:53
* @Entity com.yanyan.domain.PostReply
*/
public interface PostReplyMapper extends BaseMapper<PostReply>{
    List<PostReplyDTO> selectPostReplyWithUserInfo(@Param("postId") Long postId);
}




