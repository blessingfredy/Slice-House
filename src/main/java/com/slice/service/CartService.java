package com.slice.service;

import java.util.List;

import com.slice.model.CartItem;

public interface CartService {

    void addToCart(Long pizzaId);

    void removeFromCart(Long pizzaId);

    List<CartItem> getCartItems();

    double getTotal();

    void clearCart();
    
    double getSubtotal();
    double getTax();
    double getDeliveryFee();
    double getGrandTotal();
    void decreaseQuantity(Long pizzaId);
    int getItemCount();


}
