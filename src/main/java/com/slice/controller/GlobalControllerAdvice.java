package com.slice.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.slice.service.CartService;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired 
    private CartService cartService;

    @ModelAttribute("cartCount")
    public int cartCount(Principal principal) {

        if (principal == null) return 0;

        return cartService.getItemCount();  
    }
}
