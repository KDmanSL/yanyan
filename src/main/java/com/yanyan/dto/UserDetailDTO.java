package com.yanyan.dto;

import lombok.Data;

@Data
public class UserDetailDTO {
    private Long id;
    private String schoolName;
    private String majorName;
    private Double score;
    private Integer session;
}
