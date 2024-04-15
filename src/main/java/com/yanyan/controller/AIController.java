package com.yanyan.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AIController {

    @Resource
    ChatClient chatClient;

    @GetMapping("/simple")
    public Map<String, String> completion(@RequestParam(value = "message", defaultValue = "给我讲个笑话") String message) {
        var value=chatClient.call(message);
        System.out.println(value);
        return Map.of("generation",value );
    }
}