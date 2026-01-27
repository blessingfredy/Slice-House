package com.slice.service;

public interface EmailService {
    void sendVerificationEmail(String to, String link);
}

