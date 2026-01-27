package com.slice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.slice.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
