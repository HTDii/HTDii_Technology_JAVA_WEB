package com.javawebbackend.demo.repository;

import com.javawebbackend.demo.model.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderHistoryRepository
        extends JpaRepository<OrderHistory, Long> {

    List<OrderHistory> findAll();

    List<OrderHistory> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<OrderHistory> findByUserIdAndProductIdAndCreatedAt(
            Long userId,
            Long productId,
            LocalDateTime createdAt);
}