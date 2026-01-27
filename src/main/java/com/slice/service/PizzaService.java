package com.slice.service;

import java.util.List;

import com.slice.model.Pizza;

public interface PizzaService {

    Pizza savePizza(Pizza pizza);

    List<Pizza> getAllPizzas();

    Pizza getPizzaById(Long id);

    void deletePizza(Long id);
}