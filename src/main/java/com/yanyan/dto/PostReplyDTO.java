package com.yanyan.dto;

import lombok.Data;

import java.util.Date;
@Data
public class PostReplyDTO {
    private Long id;
    private Long postid;
    private Long userid;
    private String username;
    private String imgUrl;
    private String content;
    private Date replydate;
}
