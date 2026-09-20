package com.recipenetwork.backend.recipe;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recipes")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}, allowCredentials = "true")
public class RecipeScrapeController {

    private final RecipeScraper recipeScraper;

    public RecipeScrapeController(RecipeScraper recipeScraper) {
        this.recipeScraper = recipeScraper;
    }

    @PostMapping("/scrape")
    public ResponseEntity<ExternalRecipe> scrape(@RequestParam String url) {
        return ResponseEntity.ok(recipeScraper.scrape(url));
    }
}