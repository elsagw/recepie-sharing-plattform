package com.recipenetwork.backend.recipe;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class RecipeController {

    @GetMapping("/api/recipes")
    public List<Recipe> getRecipes() {
        return List.of(
                new Recipe(1L, "Spaghetti Carbonara", "Creamy pasta with pancetta and parmesan.", 4.8),
                new Recipe(2L, "Veggie Bowl", "Roasted vegetables with quinoa and tahini dressing.", 4.5),
                new Recipe(3L, "Blueberry Pancakes", "Fluffy pancakes served with maple syrup.", 4.7)
        );
    }
}
