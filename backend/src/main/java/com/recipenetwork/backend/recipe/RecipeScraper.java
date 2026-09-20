package com.recipenetwork.backend.recipe;

public interface RecipeScraper {

    ExternalRecipe scrape(String rawUrl);
}