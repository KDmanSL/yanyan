package com.yanyan.dto;


import lombok.Data;

import java.util.Objects;

@Data
public class MajorCourseDTO {
    private Long majorId;
    private String majorName;
    private Long courseId;
    private String courseName;
    private String courseDescription;
    private String courseUrl;
    private String courseImgUrl;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MajorCourseDTO that = (MajorCourseDTO) o;
        return Objects.equals(courseId, that.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId);
    }
}
