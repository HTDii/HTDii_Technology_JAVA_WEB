package com.javawebbackend.demo.service;

import com.javawebbackend.demo.model.OrderHistory;
import com.javawebbackend.demo.model.OrderStatus;
import com.javawebbackend.demo.model.User;
import com.javawebbackend.demo.repository.OrderHistoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderHistoryRepository orderRepo;

    public OrderService(OrderHistoryRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    @Transactional
    public void updateOrderStatus(
            Long orderId,
            OrderStatus status,
            User admin) {

        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            throw new RuntimeException("Permission denied");
        }

        OrderHistory order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);
        orderRepo.save(order);
    }
}