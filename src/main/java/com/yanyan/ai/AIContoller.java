package com.yanyan.ai;

import org.springframework.ai.chat.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AIContoller {

    @Autowired
    ChatClient chatClient;

    @GetMapping("/ai/simple")
    public Map<String, String> completion(@RequestParam(value = "message", defaultValue = "给我讲个笑话") String message) {

        var value=chatClient.call(message);
        System.out.println(value);
        return Map.of("generation",value );
    }
}