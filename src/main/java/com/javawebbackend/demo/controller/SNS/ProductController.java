package com.javawebbackend.demo.controller.SNS;

import com.javawebbackend.demo.model.User;
import com.javawebbackend.demo.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;

@Controller
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/manager-products")
    public String productPage(HttpSession session, Model model) {
        User admin = (User) session.getAttribute("currentUser");

        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            return "redirect:/login";
        }

        model.addAttribute("products", productService.getAllProducts(admin));
        model.addAttribute("user", admin);

        return "manager-products";
    }

    @PostMapping("/manager-products/create")
    public String createProduct(
            @RequestParam String productName,
            @RequestParam Integer quantity,
            @RequestParam(required = false) String shortDescription,
            @RequestParam(required = false) String description,
            @RequestParam Long priceToken,
            @RequestParam String status,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User admin = (User) session.getAttribute("currentUser");

        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            return "redirect:/login";
        }

        productService.createProduct(
                productName,
                quantity,
                shortDescription,
                description,
                priceToken,
                status,
                admin);
        redirectAttributes.addFlashAttribute("success", "Product created successfully!");

        return "redirect:/manager-products";
    }

    @PostMapping("/manager-products/delete")
    public String deleteProduct(
            @RequestParam Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User admin = (User) session.getAttribute("currentUser");

        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            return "redirect:/login";
        }

        productService.deleteProduct(id, admin);
        redirectAttributes.addFlashAttribute("success", "Product deleted successfully!");

        return "redirect:/manager-products";
    }

    @PostMapping("/manager-products/update")
    public String updateProduct(
            @RequestParam Long id,
            @RequestParam String productName,
            @RequestParam Integer quantity,
            @RequestParam(required = false) String shortDescription,
            @RequestParam(required = false) String description,
            @RequestParam Long priceToken,
            @RequestParam String status,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User admin = (User) session.getAttribute("currentUser");

        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            return "redirect:/login";
        }

        productService.updateProduct(
                id,
                productName,
                quantity,
                shortDescription,
                description,
                priceToken,
                status,
                admin);

        redirectAttributes.addFlashAttribute("success", "Update product successfully!");

        return "redirect:/manager-products";
    }

    @PostMapping("/manager-products/hide")
    public String hideProduct(
            @RequestParam Long id,
            HttpSession session,
            RedirectAttributes ra) {

        User admin = (User) session.getAttribute("currentUser");
        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            return "redirect:/login";
        }

        productService.hideProduct(id, admin);

        ra.addFlashAttribute("success", "Product hidden");
        return "redirect:/manager-products";
    }
}