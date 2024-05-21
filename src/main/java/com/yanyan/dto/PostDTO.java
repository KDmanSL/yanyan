package com.yanyan.dto;

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
    private Date postdate;
    private Long like;
    private List<PostReplyDTO> postReplyList;
}
