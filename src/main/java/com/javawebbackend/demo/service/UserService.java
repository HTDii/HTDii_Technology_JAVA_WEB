package com.javawebbackend.demo.service;

import java.util.List;

import com.javawebbackend.demo.model.TokenHistory;
import com.javawebbackend.demo.model.User;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.javawebbackend.demo.repository.TokenHistoryRepository;
import com.javawebbackend.demo.repository.UserRepository;

// import io.micrometer.common.lang.NonNull;

@Service
public class UserService {
    private final UserRepository repo;
    private final TokenHistoryRepository historyRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repo,
            TokenHistoryRepository historyRepo,
            PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.historyRepo = historyRepo;
        this.passwordEncoder = passwordEncoder;
    }

    // List
    public List<User> getAllUser() {
        return repo.findAll();

    }

    // Delete
    public void deleteUser(Long id, User currentUser) {

        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }

        if (!"ADMIN".equals(currentUser.getRole())) {
            throw new RuntimeException("Permission denied");
        }

        User target = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (currentUser.getId().equals(target.getId())) {
            throw new RuntimeException("Admin cannot delete himself");
        }

        repo.delete(target);
    }

    // Set admin
    public void makeAdmin(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }
        User user = repo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole("ADMIN");
        repo.save(user);
    }

    // set method changePassword

    public void changePassword(Long id, String newPassword) {

        if (id == null || newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("Invalid password data");
        }

        User user = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        repo.save(user);
    }

    // forgot/resset password
    public void resetPasswordByInfo(
            String username,
            String email,
            String phone,
            String newPassword) {

        User user = repo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getEmail().equals(email) || !user.getPhone().equals(phone)) {
            throw new RuntimeException("Information does not match");
        }

        user.setPassword(passwordEncoder.encode(newPassword)); // ✅ encode
        repo.save(user);
    }

    // ===== AUTO SUBTRACT TOKEN WHEN USING SERVICE =====
    public void updateBalance(
            Long userId,
            Long newBalance,
            User currentUser,
            String note) {

        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            throw new RuntimeException("Permission denied");
        }

        if (userId == null || newBalance == null || newBalance < 0) {
            throw new IllegalArgumentException("Invalid balance value");
        }

        User user = repo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        long oldBalance = user.getTokenBalance();
        long changeAmount = newBalance - oldBalance;

        user.setTokenBalance(newBalance);
        repo.save(user);

        // action_type update

        String actionType;
        if (changeAmount > 0) {
            actionType = "ADMIN_ADD";
        } else if (changeAmount < 0) {
            actionType = "ADMIN_DEDUCT";
        } else {
            actionType = "SYSTEM_ADJUST";
        }
        TokenHistory history = new TokenHistory();
        history.setUserId(userId);
        history.setAdminId(currentUser.getId());
        history.setOldBalance(oldBalance);
        history.setNewBalance(newBalance);
        history.setChangeAmount(changeAmount);
        history.setActionType(actionType);

        history.setNote(
                (note == null || note.trim().isEmpty())
                        ? ""
                        : note.trim());

        historyRepo.save(history);
    }

    // ===== ADMIN READ HISTORY OF A USER =====
    public List<TokenHistory> getHistoryByUserId(Long userId, User admin) {

        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            throw new RuntimeException("Permission denied");
        }

        return historyRepo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // ===== USER READ OWN TOKEN HISTORY =====
    public List<TokenHistory> getHistoryForCurrentUser(User user) {

        if (user == null) {
            throw new RuntimeException("Not logged in");
        }

        return historyRepo.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

}
