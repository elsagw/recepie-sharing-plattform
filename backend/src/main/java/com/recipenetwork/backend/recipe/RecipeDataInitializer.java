package com.recipenetwork.backend.recipe;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RecipeDataInitializer {

    @Bean
    CommandLineRunner initializeRecipes(RecipeRepository recipeRepository) {
        return args -> {
            if (recipeRepository.count() == 0) {
                recipeRepository.saveAll(java.util.List.of(
                    new Recipe(1L, "Spaghetti Carbonara", "Creamy pasta with pancetta and parmesan.", 4.8),
                    new Recipe(2L, "Veggie Bowl", "Roasted vegetables with quinoa and tahini dressing.", 4.5),
                    new Recipe(3L, "Blueberry Pancakes", "Fluffy pancakes served with maple syrup.", 4.7)
                ));
            }
        };
    }
}