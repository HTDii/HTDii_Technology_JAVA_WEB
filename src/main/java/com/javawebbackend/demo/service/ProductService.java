package com.javawebbackend.demo.service;

import com.javawebbackend.demo.model.Product;
import com.javawebbackend.demo.model.User;
import com.javawebbackend.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public Product getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Product id must not be null");
        }

        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    private void checkAdmin(User user) {
        if (user == null || !"ADMIN".equals(user.getRole())) {
            throw new RuntimeException("Permission denied");
        }
    }

    // ADMIN: tạo product
    public void createProduct(
            String productName,
            Integer quantity,
            String shortDescription,
            String description,
            Long priceToken,
            String status,
            User admin) {
        checkAdmin(admin);

        if (productName == null || productName.isBlank() || priceToken == null || priceToken < 0) {
            throw new IllegalArgumentException("Invalid product data");
        }

        Product p = new Product();
        p.setProductName(productName);
        p.setQuantity(quantity);
        p.setShortDescription(shortDescription);
        p.setDescription(description);
        p.setPriceToken(priceToken);
        p.setStatus("ACTIVE");

        repo.save(p);
    }

    // ADMIN: list all
    public List<Product> getAllProducts(User admin) {
        checkAdmin(admin);
        return repo.findAll();
    }

    // USER: chỉ lấy sản phẩm active
    public List<Product> getActiveProducts() {
        return repo.findByStatus("ACTIVE");
    }

    // ADMIN: ẩn sản phẩm (không xoá)
    public void hideProduct(Long id, User admin) {

        if (id == null) {
            throw new IllegalArgumentException("Product id must not be null");
        }

        checkAdmin(admin);

        Product p = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if ("HIDDEN".equals(p.getStatus())) {
            return; // đã ẩn rồi thì thôi, không làm gì
        }

        p.setStatus("HIDDEN");
        repo.save(p);
    }

    // ADMIN: xoá sản phẩm
    public void deleteProduct(Long id, User admin) {

        if (id == null) {
            throw new IllegalArgumentException("Product id must not be null");
        }

        checkAdmin(admin);

        if (!repo.existsById(id)) {
            throw new RuntimeException("Product not found");
        }

        repo.deleteById(id);
    }

    // ADMIN: update sản phẩm
    public void updateProduct(
            Long id,
            String productName,
            Integer quantity,
            String shortDescription,
            String description,
            Long priceToken,
            String status,
            User admin) {

        if (id == null) {
            throw new IllegalArgumentException("Product id must not be null");
        }

        checkAdmin(admin);

        if (productName == null || productName.isBlank()
                || priceToken == null || priceToken < 0) {
            throw new IllegalArgumentException("Invalid product data");
        }

        Product p = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        p.setProductName(productName);
        p.setQuantity(quantity);
        p.setShortDescription(shortDescription);
        p.setDescription(description);
        p.setPriceToken(priceToken);
        p.setStatus(status);

        repo.save(p);
    }
}