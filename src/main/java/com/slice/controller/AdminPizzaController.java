package com.slice.controller;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.slice.model.Pizza;
import com.slice.service.PizzaService;

@Controller
@RequestMapping("/admin/pizzas")
public class AdminPizzaController {

    @Autowired
    private PizzaService pizzaService;

    @GetMapping
    public String listPizzas(Model model) {
        List<Pizza> pizzas = pizzaService.getAllPizzas();

        long availableCount = pizzas.stream()
                                     .filter(Pizza::isAvailable)
                                     .count();

        long unavailableCount = pizzas.size() - availableCount;

        model.addAttribute("pizzas", pizzas);
        model.addAttribute("availableCount", availableCount);
        model.addAttribute("unavailableCount", unavailableCount);

        return "admin-pizzas";
    }


    // Show add form
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("pizza", new Pizza());
        return "pizza-form";
    }

    // Save pizza
    @PostMapping("/save")
    public String savePizza(
            @ModelAttribute("pizza") Pizza pizza,
            @RequestParam("imageFile") MultipartFile file) throws IOException {

        if (!file.isEmpty()) {

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            Path uploadPath = Paths.get("uploads");

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Files.copy(file.getInputStream(),
                       uploadPath.resolve(fileName),
                       StandardCopyOption.REPLACE_EXISTING);

            pizza.setImageName(fileName);
        }
        else if (pizza.getId() != null) {

            Pizza existing = pizzaService.getPizzaById(pizza.getId());
            pizza.setImageName(existing.getImageName());
        }

        pizzaService.savePizza(pizza);
        return "redirect:/admin/pizzas";
    }


    // Show edit form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("pizza", pizzaService.getPizzaById(id));
        return "pizza-form";
    }

    // Delete pizza
    @GetMapping("/delete/{id}")
    public String deletePizza(@PathVariable Long id) {
        pizzaService.deletePizza(id);
        return "redirect:/admin/pizzas";
    }
}