package com.javawebbackend.demo.controller.SNS;

import com.javawebbackend.demo.model.Product;
import com.javawebbackend.demo.model.User;
import com.javawebbackend.demo.service.BuyerService;
import com.javawebbackend.demo.service.ProductService;
import com.javawebbackend.demo.repository.OrderHistoryRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;

@Controller
public class BuyerController {

    private final ProductService productService;
    private final BuyerService buyerService;
    private final OrderHistoryRepository orderHistoryRepo;

    public BuyerController(ProductService productService,
            BuyerService buyerService,
            OrderHistoryRepository orderHistoryRepo) {

        this.productService = productService;
        this.buyerService = buyerService;
        this.orderHistoryRepo = orderHistoryRepo;
    }

    // HIỂN THỊ TRANG XÁC NHẬN
    @GetMapping("/order")
    public String orderPage(
            @RequestParam Long productId,
            HttpSession session,
            Model model) {

        User user = (User) session.getAttribute("currentUser");
        if (user == null)
            return "redirect:/login";

        Product product = productService.getById(productId);

        model.addAttribute("product", product);
        model.addAttribute("user", user);
        return "order";
    }

    @PostMapping("/order/confirm")
    public String confirmOrder(
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            @RequestParam String note,
            HttpSession session,
            RedirectAttributes ra) {

        User user = (User) session.getAttribute("currentUser");

        try {
            buyerService.buyProduct(productId, quantity, note, user);
            ra.addFlashAttribute("success", "Purchase successful!");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/order?productId=" + productId;
    }

    // ===== ORDER HISTORY =====
    @GetMapping("/order-history")
    public String orderHistory(HttpSession session, Model model) {

        User user = (User) session.getAttribute("currentUser");
        if (user == null)
            return "redirect:/login";

        model.addAttribute("user", user);
        model.addAttribute(
                "orderHistories",
                orderHistoryRepo.findByUserIdOrderByCreatedAtDesc(user.getId()));

        return "order-history";
    }
}
