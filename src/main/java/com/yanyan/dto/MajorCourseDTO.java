package com.yanyan.dto;


import lombok.Data;

@Data
public class MajorCourseDTO {
    private Long majorId;
    private String majorName;
    private Long courseId;
    private String courseName;
    private String courseDescription;
    private String courseUrl;
    private String courseImgUrl;
}
