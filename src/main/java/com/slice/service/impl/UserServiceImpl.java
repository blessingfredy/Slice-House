package com.slice.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.slice.model.Role;
import com.slice.model.User;
import com.slice.model.VerificationToken;
import com.slice.repository.RoleRepository;
import com.slice.repository.UserRepository;
import com.slice.repository.VerificationTokenRepository;
import com.slice.service.EmailService;
import com.slice.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Override
    public User registerCustomer(User user) {

        Role role = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseThrow(() -> new RuntimeException("ROLE_CUSTOMER not found"));

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(false);
        user.getRoles().add(role);

        User savedUser = userRepository.save(user);

        // 🔐 Generate token
        String token = UUID.randomUUID().toString();

        VerificationToken vt = new VerificationToken();
        vt.setToken(token);
        vt.setUser(savedUser);
        vt.setExpiryDate(LocalDateTime.now().plusHours(24));

        tokenRepository.save(vt);

        // 🔗 Send email
        String link = "http://localhost:8080/verify?token=" + token;
        emailService.sendVerificationEmail(savedUser.getEmail(), link);

        return savedUser;
    }

    @Override
    public boolean verifyUser(String token) {

        VerificationToken vt = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (vt.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        User user = vt.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        tokenRepository.delete(vt);

        return true;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}

