package com.slice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import com.slice.service.CartService;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/cart")
    public String viewCart(Model model) {

        model.addAttribute("cartItems", cartService.getCartItems());
        model.addAttribute("subtotal", cartService.getSubtotal());
        model.addAttribute("tax", cartService.getTax());
        model.addAttribute("deliveryFee", cartService.getDeliveryFee());
        model.addAttribute("grandTotal", cartService.getGrandTotal());

        return "cart";
    }


    @GetMapping("/cart/add/{id}")
    public String addToCart(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        cartService.addToCart(id);

        redirectAttributes.addFlashAttribute("cartMessage", "Pizza added to cart!");

        return "redirect:/menu";
    }

    @GetMapping("/cart/remove/{id}")
    public String removeFromCart(@PathVariable Long id) {

        cartService.removeFromCart(id);
        return "redirect:/cart";
    }
    @GetMapping("/cart/increase/{id}")
    public String increaseQuantity(@PathVariable Long id) {

        cartService.addToCart(id);
        return "redirect:/cart";
    }
    @GetMapping("/cart/decrease/{id}")
    public String decreaseQuantity(@PathVariable Long id) {

        cartService.decreaseQuantity(id);
        return "redirect:/cart";
    }


}
