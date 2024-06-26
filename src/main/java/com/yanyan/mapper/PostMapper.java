package com.yanyan.mapper;

import com.yanyan.domain.Post;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanyan.dto.PostDTO;
import com.yanyan.dto.PostReplyDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 韶光善良君
* @description 针对表【yy_post(论坛帖子)】的数据库操作Mapper
* @createDate 2024-04-05 17:23:50
* @Entity com.yanyan.domain.Post
*/
public interface PostMapper extends BaseMapper<Post> {
    List<PostDTO> selectPostWithUserInfo();
    Post getPost(@Param("postId") Long postId);
}




