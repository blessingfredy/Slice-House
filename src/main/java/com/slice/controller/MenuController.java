package com.slice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.slice.model.Pizza;
import com.slice.service.PizzaService;

@Controller
public class MenuController {

    @Autowired
    private PizzaService pizzaService;

    @GetMapping("/menu")
    public String showMenu(Model model) {

        List<Pizza> pizzas = pizzaService.getAllPizzas();

        model.addAttribute("pizzas", pizzas);

        return "menu";
    }
}