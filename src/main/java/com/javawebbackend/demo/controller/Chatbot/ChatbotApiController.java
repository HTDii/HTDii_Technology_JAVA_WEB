package com.javawebbackend.demo.controller.Chatbot;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatbotApiController {

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/send")
    public Map<String, Object> send(@RequestBody Map<String, Object> body) {

        Map flaskRes = restTemplate.postForObject(
                "http://localhost:5001/api/chat",
                body,
                Map.class);

        // ✅ TRẢ NGUYÊN JSON TỪ FLASK
        return flaskRes;
    }
}