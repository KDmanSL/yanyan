package com.yanyan.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yanyan.domain.PostReply;
import lombok.Data;

import java.util.Date;
import java.util.List;
@Data
public class PostDTO {
    private Long id;
    private Long userid;
    private String username;
    private String imgUrl;
    private String title;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date postdate;
    private Long like;
    private Boolean isLike; // 是否已经点赞
    private List<PostReplyDTO> postReplyList;
}
