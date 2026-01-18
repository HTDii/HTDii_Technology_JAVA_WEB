package com.javawebbackend.demo.controller.Chatbot;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatbotPageController {

    @GetMapping("/chatbot")
    public String chatbotPage() {
        return "chatbot/chatbot";
    }
}