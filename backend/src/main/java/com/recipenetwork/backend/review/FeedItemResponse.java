package com.recipenetwork.backend.review;

import java.time.Instant;

public record FeedItemResponse(
        String username,
        Long recipeId,
        String sourceUrl,
        String title,
        String imageUrl,
        String domain,
        Integer rating,
        String comment,
        Instant createdAt,
        boolean saved) {

    public static FeedItemResponse from(Review review, boolean saved) {
        return new FeedItemResponse(
                review.getUser().getUsername(),
                review.getRecipe().getId(),
                review.getRecipe().getSourceUrl(),
                review.getRecipe().getTitle(),
                review.getRecipe().getImageUrl(),
                review.getRecipe().getDomain(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt(),
                saved);
    }
}