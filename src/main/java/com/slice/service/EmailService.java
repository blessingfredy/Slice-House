package com.slice.service;

import java.io.UnsupportedEncodingException;

import com.slice.model.User;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendVerificationEmail(User user, String to, String link) throws MessagingException, UnsupportedEncodingException;
}

