package com.recipenetwork.backend.review;

import com.recipenetwork.backend.auth.User;
import com.recipenetwork.backend.recipe.ExternalRecipe;
import com.recipenetwork.backend.saved.SavedRecipeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private ReviewRepository reviewRepository;

    @MockBean
    private SavedRecipeService savedRecipeService;

    @Test
    void shouldCreateReviewForAuthenticatedUser() throws Exception {
        User user = new User("elsa", "elsa@example.com", "hash");
        ExternalRecipe recipe = new ExternalRecipe("https://ica.se/soup", "Tomato Soup", "https://ica.se/soup.jpg", "ica.se");
        Review review = new Review(user, recipe, 9, "Very good");
        when(reviewService.create(any(), any())).thenReturn(review);

        mockMvc.perform(post("/api/reviews")
                        .principal(UsernamePasswordAuthenticationToken.authenticated("elsa", null, List.of()))
                        .contentType(APPLICATION_JSON)
                        .content("{\"recipeId\":1,\"rating\":9,\"comment\":\"Very good\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("elsa"))
                .andExpect(jsonPath("$.title").value("Tomato Soup"))
                .andExpect(jsonPath("$.rating").value(9));
    }

    @Test
    void shouldReturnNewestReviewsAsFeedItems() throws Exception {
        User user = new User("elsa", "elsa@example.com", "hash");
        ExternalRecipe recipe = new ExternalRecipe("https://ica.se/soup", "Tomato Soup", "https://ica.se/soup.jpg", "ica.se");
        Review review = new Review(user, recipe, 9, "Very good");
        when(reviewRepository.findAllByOrderByCreatedAtDesc(any()))
                .thenReturn(new PageImpl<>(List.of(review)));

        mockMvc.perform(get("/api/feed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$.content[0].comment").value("Very good"))
                .andExpect(jsonPath("$.content[0].createdAt").isNotEmpty());
    }
}