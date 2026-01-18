package com.javawebbackend.demo.service;

import com.javawebbackend.demo.model.OrderHistory;
import com.javawebbackend.demo.model.Product;
import com.javawebbackend.demo.model.TokenHistory;
import com.javawebbackend.demo.model.User;
import com.javawebbackend.demo.repository.OrderHistoryRepository;
import com.javawebbackend.demo.repository.ProductRepository;
import com.javawebbackend.demo.repository.TokenHistoryRepository;
import com.javawebbackend.demo.repository.UserRepository;
import com.javawebbackend.demo.model.OrderStatus;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class BuyerService {

    private final ProductRepository productRepo;
    private final UserRepository userRepo;
    private final OrderHistoryRepository orderRepo;
    private final TokenHistoryRepository historyRepo;

    public BuyerService(ProductRepository productRepo,
            UserRepository userRepo,
            OrderHistoryRepository orderRepo,
            TokenHistoryRepository historyRepo) {
        this.productRepo = productRepo;
        this.userRepo = userRepo;
        this.orderRepo = orderRepo;
        this.historyRepo = historyRepo;
    }

    @Transactional
    public void buyProduct(Long productId, Integer quantity, String note, User user) {

        if (user == null) {
            throw new RuntimeException("Please login first");
        }

        if (productId == null) {
            throw new RuntimeException("Invalid product");
        }

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!"ACTIVE".equals(product.getStatus())) {
            throw new RuntimeException("Product is not available");
        }

        if (product.getQuantity() < quantity) {
            throw new RuntimeException("Out of stock");
        }

        if (product.getPriceToken() == null) {
            throw new RuntimeException("Invalid product price");
        }

        long cost = product.getPriceToken() * quantity;

        if (user.getTokenBalance() < cost) {
            throw new RuntimeException("Not enough tokens");
        }
        if (note == null || note.trim().isEmpty()) {
            throw new RuntimeException("Note is required");
        }

        // ===== TOKEN BEFORE =====
        long oldBalance = user.getTokenBalance();
        long newBalance = oldBalance - cost;

        // trừ token
        user.setTokenBalance(newBalance);
        userRepo.save(user);

        // trừ số lượng
        product.setQuantity(product.getQuantity() - quantity);
        if (product.getQuantity() == 0) {
            product.setStatus("INACTIVE");
        }
        productRepo.save(product);

        // ===== ORDER HISTORY =====
        OrderHistory order = new OrderHistory();
        order.setUserId(user.getId());
        order.setProductId(product.getId());
        order.setProductName(product.getProductName());
        order.setPriceToken(cost);
        order.setQuantity(quantity);
        order.setStatus(OrderStatus.PENDING);
        order.setNote(note.trim());
        orderRepo.save(order);

        // ===== TOKEN HISTORY (USER USE TOKEN) =====
        TokenHistory history = new TokenHistory();
        history.setUserId(user.getId());
        history.setOldBalance(oldBalance);
        history.setNewBalance(newBalance);
        history.setChangeAmount(-cost);
        history.setActionType("BUY_PRODUCT");
        history.setNote("Buy product: " + product.getProductName());

        historyRepo.save(history);
    }
}