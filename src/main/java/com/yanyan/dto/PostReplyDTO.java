package com.yanyan.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date replydate;
}
