package com.yanyan.dto;

import lombok.Data;

@Data
public class RegisterFormDTO {
    private String username;
    private String password;
    private String email;
    private String code;
}
