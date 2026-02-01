package com.slice.controller;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.slice.model.PasswordResetToken;
import com.slice.model.User;
import com.slice.repository.PasswordResetTokenRepository;
import com.slice.repository.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;

@Controller
public class ForgotPasswordController {

    @Autowired private UserRepository userRepo;
    @Autowired private PasswordResetTokenRepository tokenRepo;
    @Autowired private JavaMailSender mailSender;

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    @Transactional
    public String processForgotPassword(
            @RequestParam String email,
            RedirectAttributes ra) {

        User user = userRepo.findByEmail(email).orElse(null);

        if (user == null) {
            ra.addFlashAttribute("error", "Email not found");
            return "redirect:/forgot-password";
        }

        // 🔥 THIS NOW WORKS
        tokenRepo.deleteByUser(user);

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));

        tokenRepo.save(resetToken);

        sendResetEmail(user.getEmail(), token);

        ra.addFlashAttribute("message",
                "Password reset link sent to your email");

        return "redirect:/login";
    }

    private void sendResetEmail(String email, String token) {

        String resetLink = "http://localhost:8080/reset-password?token=" + token;

        String html = """
            <div style="font-family:Segoe UI,Arial,sans-serif;
                        background:#0f0f0f;
                        padding:30px;
                        color:#ffffff;">
            
                <div style="max-width:600px;
                            margin:auto;
                            background:#1a1a1a;
                            border-radius:16px;
                            padding:30px;
                            box-shadow:0 10px 40px rgba(255,107,53,0.3);
                            border:1px solid rgba(255,107,53,0.3);">

                    <h1 style="text-align:center;
                               background:linear-gradient(135deg,#ff6b35,#f7931e);
                               -webkit-background-clip:text;
                               -webkit-text-fill-color:transparent;">
                        Slice House 🍕
                    </h1>

                    <p style="font-size:16px;color:#ddd;">
                        Hey there 👋,
                    </p>

                    <p style="font-size:15px;color:#ccc;">
                        We received a request to reset your password.
                        Click the button below to set a new one.
                    </p>

                    <div style="text-align:center;margin:30px 0;">
                        <a href="%s"
                           style="background:linear-gradient(135deg,#ff6b35,#f7931e);
                                  padding:14px 26px;
                                  color:#fff;
                                  text-decoration:none;
                                  border-radius:30px;
                                  font-weight:600;
                                  display:inline-block;">
                            Reset Password
                        </a>
                    </div>

                    <p style="font-size:14px;color:#aaa;">
                        ⏱ This link will expire in <b>15 minutes</b>.
                    </p>

                    <p style="font-size:14px;color:#777;">
                        If you didn’t request this, you can safely ignore this email.
                    </p>

                    <hr style="border:none;border-top:1px solid rgba(255,255,255,0.1);margin:25px 0;"/>

                    <p style="font-size:13px;color:#666;text-align:center;">
                        © 2026 Slice House · Fresh pizzas, fast delivery
                    </p>
                </div>
            </div>
            """.formatted(resetLink);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("🍕 Reset your Slice House password");
            helper.setText(html, true); // TRUE = HTML

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send reset email", e);
        }
    }

}
	
