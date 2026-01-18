package com.javawebbackend.demo.controller.SNS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.javawebbackend.demo.service.AuthService;
import com.javawebbackend.demo.service.UserService;
import com.javawebbackend.demo.service.ProductService;
import com.javawebbackend.demo.model.User;

import jakarta.servlet.http.HttpSession;

import org.springframework.ui.Model;

@Controller
public class AccountController {
    @Autowired
    private AuthService authService;
    private final UserService userService;
    private final ProductService productService;

    public AccountController(UserService userService,
            ProductService productService) {
        this.userService = userService;
        this.productService = productService;
    }

    // // HomePage
    // @GetMapping("/")
    // public String home() {
    // return "redirect:/home";
    // }

    // Registerpage
    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", currentUser);

        // ===== ADD USER TOKEN HISTORY =====
        model.addAttribute(
                "tokenHistories",
                userService.getHistoryForCurrentUser(currentUser));

        /////// ADD product
        model.addAttribute("products", productService.getActiveProducts());
        if (currentUser != null && !"ADMIN".equals(currentUser.getRole())) {
            model.addAttribute("addproducts", userService.getHistoryForCurrentUser(currentUser));
        }
        return "home";
    }

    // Loginpage
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // addAccount logic
    @PostMapping("/register")
    public String register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email,
            @RequestParam String phone,
            Model model) {

        try {
            authService.register(username, password, email, phone);

            model.addAttribute(
                    "success",
                    "Registration successful. Please go back to login to sign in.");

            return "register";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }

    // forgot/resetpassword
    @PostMapping("/forgot-password")
    public String forgotPassword(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String newPassword,
            Model model) {

        try {
            userService.resetPasswordByInfo(username, email, phone, newPassword);
            model.addAttribute("success", "Password has been reset. Please login.");
            return "forgot-password";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "forgot-password";
        }
    }

    // account change password
    @GetMapping("/account")
    public String account(
            HttpSession session,
            Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null)
            return "redirect:/login";
        model.addAttribute("user", user);
        return "account";
    }

    @PostMapping("/account/change-password")
    public String changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            HttpSession session,
            Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }
        if (!user.getPassword().equals(oldPassword)) {
            model.addAttribute("error", "Old password incorrect!");
            model.addAttribute("user", user);
            return "account";
        }
        userService.changePassword(user.getId(), newPassword);
        user.setPassword(newPassword); // Update newPassword
        session.setAttribute("currentUser", user);

        model.addAttribute("success", "Password updated");
        model.addAttribute("user", user);
        return "account";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

}
