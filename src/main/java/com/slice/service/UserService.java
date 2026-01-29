package com.slice.service;

import java.io.UnsupportedEncodingException;

import com.slice.model.User;

import jakarta.mail.MessagingException;

public interface UserService {
    User registerCustomer(User user) throws MessagingException, UnsupportedEncodingException;
    User findByEmail(String email);
    boolean verifyUser(String token);
}
