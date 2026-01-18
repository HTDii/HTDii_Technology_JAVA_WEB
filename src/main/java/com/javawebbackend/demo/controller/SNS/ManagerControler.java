
package com.javawebbackend.demo.controller.SNS;

import com.javawebbackend.demo.model.OrderStatus;
import com.javawebbackend.demo.model.TokenHistory;
import com.javawebbackend.demo.model.User;
import com.javawebbackend.demo.service.UserService;
import com.javawebbackend.demo.service.OrderService;
import com.javawebbackend.demo.repository.OrderHistoryRepository;

import jakarta.servlet.http.HttpSession;

import java.util.HashMap;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
public class ManagerControler {

    private final UserService userService;
    private final OrderService orderService;
    private final OrderHistoryRepository orderHistoryRepository;

    public ManagerControler(UserService userService,
            OrderService orderService,
            OrderHistoryRepository orderHistoryRepository) {
        this.userService = userService;
        this.orderService = orderService;
        this.orderHistoryRepository = orderHistoryRepository;
    }

    @GetMapping("/manager")
    public String manager(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            return "redirect:/login";
        }

        if (!"ADMIN".equals(currentUser.getRole())) {
            return "redirect:/";
        }

        List<User> users = userService.getAllUser();

        // ===== BUILD HISTORY MAP =====
        Map<Long, List<TokenHistory>> tokenHistoryMap = new HashMap<>();

        for (User u : users) {
            tokenHistoryMap.put(
                    u.getId(),
                    userService.getHistoryByUserId(u.getId(), currentUser));
        }

        model.addAttribute("users", users);
        model.addAttribute("user", currentUser);
        model.addAttribute("tokenHistoryMap", tokenHistoryMap);

        return "manager";
    }

    // ===== DELETE USER =====
    @PostMapping("/manager/delete")
    public String delete(@RequestParam Long id, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            return "redirect:/login";
        }

        userService.deleteUser(id, currentUser);
        return "redirect:/manager";
    }

    // ===== MAKE ADMIN =====
    @PostMapping("/manager/make-admin")
    public String makeAdmin(@RequestParam Long id, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            return "redirect:/";
        }

        userService.makeAdmin(id);
        return "redirect:/manager";
    }

    @PostMapping("/manager/update-balance")
    public String updateBalance(
            @RequestParam Long id,
            @RequestParam Long balance,
            @RequestParam(required = false) String note,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        System.out.println("===== NOTE FROM FORM =====");
        System.out.println(note);

        User admin = (User) session.getAttribute("currentUser");

        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            redirectAttributes.addFlashAttribute(
                    "error", "Permission denied");
            return "redirect:/";
        }

        try {
            userService.updateBalance(id, balance, admin, note);

            redirectAttributes.addFlashAttribute(
                    "success", "Update token successfully");

        } catch (RuntimeException e) {

            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());
        }

        return "redirect:/manager";
    }

    @GetMapping("/manager/history")
    public String viewHistory(
            @RequestParam Long userId,
            HttpSession session,
            Model model) {

        User admin = (User) session.getAttribute("currentUser");

        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            return "redirect:/login";
        }

        model.addAttribute("users", userService.getAllUser());
        model.addAttribute("user", admin);
        model.addAttribute("histories",
                userService.getHistoryByUserId(userId, admin));

        model.addAttribute("selectedUserId", userId);

        return "manager";
    }

    @GetMapping("/admin-order-manager")
    public String adminOrderManager(HttpSession session, Model model) {

        User admin = (User) session.getAttribute("currentUser");

        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            return "redirect:/login";
        }

        model.addAttribute("user", admin);
        model.addAttribute("orders", orderHistoryRepository.findAll());

        return "admin-order-manager";
    }

    @PostMapping("/admin/order/update-status")
    public String updateStatus(
            @RequestParam Long orderId,
            @RequestParam String status,
            HttpSession session,
            RedirectAttributes ra) {

        User admin = (User) session.getAttribute("currentUser");

        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            ra.addFlashAttribute("error", "Permission denied");
            return "redirect:/login";
        }

        try {
            OrderStatus newStatus = OrderStatus.valueOf(status);

            orderService.updateOrderStatus(orderId, newStatus, admin);

            ra.addFlashAttribute("success", "Update success");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", "Invalid status value: " + status);
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin-order-manager";
    }
}