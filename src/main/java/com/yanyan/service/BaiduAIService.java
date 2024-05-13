package com.yanyan.service;


import com.yanyan.dto.Result;
import org.springframework.web.multipart.MultipartFile;

public interface BaiduAIService {

    String actionOcr(MultipartFile multipartFile);

    void createGroupMQList();
}
