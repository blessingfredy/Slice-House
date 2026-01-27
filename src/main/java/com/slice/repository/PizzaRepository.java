package com.slice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.slice.model.Pizza;


public interface PizzaRepository extends JpaRepository<Pizza, Long> {
}