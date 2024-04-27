package com.yanyan.dto;

import lombok.Data;

@Data
public class UserDetailDTO {
    private String userName;
    private String imgUrl;
    private String schoolName;
    private String majorName;
    private Double score;
    private Integer session;
}
