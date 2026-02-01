package com.slice.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.slice.model.PasswordResetToken;
import com.slice.model.User;
import com.slice.repository.PasswordResetTokenRepository;
import com.slice.repository.UserRepository;

@Controller
public class ResetPasswordController {

    @Autowired private PasswordResetTokenRepository tokenRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder passwordEncoder;

    @GetMapping("/reset-password")
    public String resetPasswordPage(
            @RequestParam String token,
            Model model) {

        PasswordResetToken resetToken =
                tokenRepo.findByToken(token).orElse(null);

        if (resetToken == null ||
            resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {

            model.addAttribute("error", "Invalid or expired token");
            return "login";
        }

        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String saveNewPassword(
            @RequestParam String token,
            @RequestParam String password,
            RedirectAttributes ra) {

        PasswordResetToken resetToken =
                tokenRepo.findByToken(token).orElse(null);

        if (resetToken == null) {
            ra.addFlashAttribute("error", "Invalid token");
            return "redirect:/login";
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(password));
        userRepo.save(user);

        tokenRepo.delete(resetToken);

        ra.addFlashAttribute("message",
                "Password updated successfully. Login now.");

        return "redirect:/login";
    }
}
	
