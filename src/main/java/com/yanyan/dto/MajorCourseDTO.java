package com.yanyan.dto;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Objects;

@Data
public class MajorCourseDTO {
    private Long majorId;
    private String majorName;
    @JsonSerialize(using = ToStringSerializer.class)
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
