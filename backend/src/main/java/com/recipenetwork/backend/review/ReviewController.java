package com.recipenetwork.backend.review;

import com.recipenetwork.backend.saved.SavedRecipeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}, allowCredentials = "true")
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;
    private final SavedRecipeService savedRecipeService;

    public ReviewController(ReviewService reviewService, ReviewRepository reviewRepository,
                            SavedRecipeService savedRecipeService) {
        this.reviewService = reviewService;
        this.reviewRepository = reviewRepository;
        this.savedRecipeService = savedRecipeService;
    }

    @PostMapping("/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public FeedItemResponse create(@Valid @RequestBody ReviewRequest request, Authentication authentication) {
        Review review = reviewService.create(requireUsername(authentication), request);
        return FeedItemResponse.from(review, savedRecipeService.isSaved(authentication.getName(), review.getRecipe().getId()));
    }

    @PutMapping("/reviews/{recipeId}")
    public FeedItemResponse update(@PathVariable Long recipeId, @Valid @RequestBody UpdateReviewRequest request,
                                   Authentication authentication) {
        Review review = reviewService.update(requireUsername(authentication), recipeId, request);
        return FeedItemResponse.from(review, savedRecipeService.isSaved(authentication.getName(), recipeId));
    }

    @GetMapping("/feed")
    public Page<FeedItemResponse> feed(@PageableDefault(size = 20) Pageable pageable,
                                       Authentication authentication) {
        String username = authenticationUsername(authentication);
        return reviewRepository.findAllByOrderByCreatedAtDesc(pageable)
            .map(review -> FeedItemResponse.from(review,
                savedRecipeService.isSaved(username, review.getRecipe().getId())));
    }

    private String requireUsername(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
        }
        return authentication.getName();
    }

    private String authenticationUsername(Authentication authentication) {
        return authentication == null || !authentication.isAuthenticated() ? null : authentication.getName();
    }
}