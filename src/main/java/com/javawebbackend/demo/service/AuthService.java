package com.javawebbackend.demo.service;

// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
// import org.springframework.web.bind.annotation.RequestParam;

import com.javawebbackend.demo.model.User;
import com.javawebbackend.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(String username,
            String password,
            String email,
            String phone) {

        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }

        if (userRepository.existsByPhone(phone)) {
            throw new RuntimeException("Phone number already registered");
        }

        User user = new User();
        user.setUsername(username);

        // 🔐 HASH PASSWORD
        user.setPassword(passwordEncoder.encode(password));

        user.setEmail(email);
        user.setPhone(phone);
        user.setRole("USER");

        userRepository.save(user);
    }

    // login
    public User login(String username, String password) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Wrong password");
        }

        return user;
    }

}
