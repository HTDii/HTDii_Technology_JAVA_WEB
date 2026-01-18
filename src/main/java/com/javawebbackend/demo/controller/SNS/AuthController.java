package com.javawebbackend.demo.controller.SNS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.javawebbackend.demo.service.AuthService;
import com.javawebbackend.demo.model.User;

import jakarta.servlet.http.HttpSession;

import org.springframework.ui.Model;

@Controller
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public String doLogin(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) {
        User user = authService.login(username, password);
        if (user == null) {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
        // Save who is logging in session
        session.setAttribute("currentUser", user);
        return "redirect:/home";
    }
}
