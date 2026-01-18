package com.javawebbackend.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.javawebbackend.demo.model.TokenHistory;
import java.util.List;

@Repository
public interface TokenHistoryRepository
        extends JpaRepository<TokenHistory, Long> {

    // Lịch sử của 1 user
    List<TokenHistory> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Lịch sử do 1 admin thao tác
    List<TokenHistory> findByAdminIdOrderByCreatedAtDesc(Long adminId);
}