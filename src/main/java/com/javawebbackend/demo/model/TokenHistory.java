// package com.javawebbackend.demo.model;

// import java.time.LocalDateTime;

// import jakarta.persistence.*;

// @Entity
// @Table(name = "token_history")
// public class TokenHistory {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     private Long userId;
//     private Long adminId;

//     private Long oldBalance;
//     private Long newBalance;
//     private Long changeAmount;

//     private String actionType; // UPDATE, SUBTRACT, SERVICE
//     private String note;

//     @Column(updatable = false)
//     private LocalDateTime createdAt = LocalDateTime.now();
// }

package com.javawebbackend.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "token_history")
public class TokenHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "admin_id")
    private Long adminId;

    @Column(name = "old_balance")
    private Long oldBalance;

    @Column(name = "new_balance")
    private Long newBalance;

    @Column(name = "change_amount")
    private Long changeAmount;

    @Column(name = "action_type")
    private String actionType;

    @Column(name = "note")
    private String note;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // ===== GETTERS =====

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getAdminId() {
        return adminId;
    }

    public Long getOldBalance() {
        return oldBalance;
    }

    public Long getNewBalance() {
        return newBalance;
    }

    public Long getChangeAmount() {
        return changeAmount;
    }

    public String getActionType() {
        return actionType;
    }

    public String getNote() {
        return note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // ===== SETTERS =====

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public void setOldBalance(Long oldBalance) {
        this.oldBalance = oldBalance;
    }

    public void setNewBalance(Long newBalance) {
        this.newBalance = newBalance;
    }

    public void setChangeAmount(Long changeAmount) {
        this.changeAmount = changeAmount;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public void setNote(String note) {
        this.note = note;
    }
}