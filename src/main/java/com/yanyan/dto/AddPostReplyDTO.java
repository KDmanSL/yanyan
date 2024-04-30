package com.yanyan.dto;

import lombok.Data;

@Data
public class AddPostReplyDTO {
    /**
     * 帖子id
     */
    Long postid;
    /**
     * 回复内容
     */
    String content;
}
