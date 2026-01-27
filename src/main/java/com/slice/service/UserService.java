package com.slice.service;

import com.slice.model.User;

public interface UserService {
    User registerCustomer(User user);
    User findByEmail(String email);
    boolean verifyUser(String token);
}
