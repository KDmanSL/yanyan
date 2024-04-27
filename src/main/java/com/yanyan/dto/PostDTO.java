package com.yanyan.dto;

import lombok.Data;

@Data
public class PostDTO {
    /**
     * 帖子标题
     */
    private String title;

    /**
     * 正文内容
     */
    private String content;
}
