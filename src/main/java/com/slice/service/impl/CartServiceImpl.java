package com.slice.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.slice.model.Cart;
import com.slice.model.CartItem;
import com.slice.model.Pizza;
import com.slice.model.User;
import com.slice.repository.CartRepository;
import com.slice.repository.PizzaRepository;
import com.slice.repository.UserRepository;
import com.slice.service.CartService;

import jakarta.servlet.http.HttpServletRequest;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired private CartRepository cartRepo;
    @Autowired private PizzaRepository pizzaRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private HttpServletRequest request;

    private String getUserEmail() {

        if (request.getUserPrincipal() == null) {
            throw new RuntimeException("User not authenticated");
        }

        return request.getUserPrincipal().getName();
    }


    private Cart getOrCreateCart() {

        String email = getUserEmail();
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        Cart cart = cartRepo.findByUserId(user.getId());

        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cartRepo.save(cart);
        }

        return cart;
    }

    @Override
    public void addToCart(Long pizzaId) {

        Cart cart = getOrCreateCart();
        Pizza pizza = pizzaRepo.findById(pizzaId).orElseThrow();

        CartItem item = cart.getItems()
                .stream()
                .filter(i -> i.getPizza().getId().equals(pizzaId))
                .findFirst()
                .orElse(null);

        if (item != null) {
            item.setQuantity(item.getQuantity() + 1);
        } else {
            item = new CartItem();
            item.setCart(cart);
            item.setPizza(pizza);
            item.setQuantity(1);
            cart.getItems().add(item);
        }

        cartRepo.save(cart);
    }

    @Override
    public void decreaseQuantity(Long pizzaId) {

        Cart cart = getOrCreateCart();

        CartItem item = cart.getItems()
                .stream()
                .filter(i -> i.getPizza().getId().equals(pizzaId))
                .findFirst()
                .orElse(null);

        if (item != null) {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
            } else {
                cart.getItems().remove(item);
            }
            cartRepo.save(cart);
        }
    }

    @Override
    public void removeFromCart(Long pizzaId) {

        Cart cart = getOrCreateCart();

        cart.getItems().removeIf(i -> i.getPizza().getId().equals(pizzaId));

        cartRepo.save(cart);
    }

    @Override
    public List<CartItem> getCartItems() {

        return getOrCreateCart().getItems();
    }

    @Override
    public double getSubtotal() {

        return getCartItems().stream()
                .mapToDouble(i -> i.getPizza().getPrice() * i.getQuantity())
                .sum();
    }

    @Override
    public double getTax() {
        return getSubtotal() * 0.08;
    }

    @Override
    public double getDeliveryFee() {
        return 20;
    }

    @Override
    public double getGrandTotal() {
        return getSubtotal() + getTax() + getDeliveryFee();
    }

    @Override
    public double getTotal() {
        return getGrandTotal();
    }

    @Override
    public void clearCart() {

        Cart cart = getOrCreateCart();
        cart.getItems().clear();
        cartRepo.save(cart);
    }

    @Override
    public int getItemCount() {

        return getCartItems()
                .stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
}
