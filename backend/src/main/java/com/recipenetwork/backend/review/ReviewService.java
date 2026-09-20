package com.recipenetwork.backend.review;

import com.recipenetwork.backend.auth.User;
import com.recipenetwork.backend.auth.UserRepository;
import com.recipenetwork.backend.recipe.ExternalRecipe;
import com.recipenetwork.backend.recipe.ExternalRecipeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ExternalRecipeRepository recipeRepository;

    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository,
                         ExternalRecipeRepository recipeRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
    }

    public Review create(String username, ReviewRequest request) {
        User user = findUser(username);
        ExternalRecipe recipe = findRecipe(request.recipeId());
        if (reviewRepository.existsByUserIdAndRecipeId(user.getId(), recipe.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Review already exists for this recipe");
        }
        return reviewRepository.save(new Review(user, recipe, request.rating(), request.comment().trim()));
    }

    public Review update(String username, Long recipeId, UpdateReviewRequest request) {
        User user = findUser(username);
        Review review = reviewRepository.findByUserIdAndRecipeId(user.getId(), recipeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
        review.update(request.rating(), request.comment().trim());
        return reviewRepository.save(review);
    }

    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required"));
    }

    private ExternalRecipe findRecipe(Long recipeId) {
        return recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));
    }
}