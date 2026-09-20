package com.recipenetwork.backend.saved;

import java.time.Instant;

public record SavedRecipeResponse(
        Long recipeId,
        String sourceUrl,
        String title,
        String imageUrl,
        String domain,
        Instant savedAt) {

    public static SavedRecipeResponse from(SavedRecipe savedRecipe) {
        return new SavedRecipeResponse(
                savedRecipe.getRecipe().getId(),
                savedRecipe.getRecipe().getSourceUrl(),
                savedRecipe.getRecipe().getTitle(),
                savedRecipe.getRecipe().getImageUrl(),
                savedRecipe.getRecipe().getDomain(),
                savedRecipe.getCreatedAt());
    }
}