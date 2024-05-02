package com.yanyan.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/ai")
public class AIController {

    @Resource
    ChatClient chatClient;

    /**
     * 简单的AI聊天
     * @param message 发送的信息
     * @return AI返回的信息
     */
    @GetMapping("/simple")
    public Map<String, String> completion(@RequestParam(value = "message", defaultValue = "给我讲个笑话") String message) {
        var value=chatClient.call(message);
        System.out.println(value);
        return Map.of("generation",value );
    }
}