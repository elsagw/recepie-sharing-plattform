package com.recipenetwork.backend.saved;

import com.recipenetwork.backend.recipe.ExternalRecipe;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SavedRecipeController.class)
@AutoConfigureMockMvc(addFilters = false)
class SavedRecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SavedRecipeService savedRecipeService;

    @Test
    void shouldReturnSavedRecipesForAuthenticatedUser() throws Exception {
        ExternalRecipe recipe = new ExternalRecipe(
                "https://ica.se/soup", "Tomato Soup", "https://ica.se/soup.jpg", "ica.se");
        SavedRecipe savedRecipe = new SavedRecipe(null, recipe);
        when(savedRecipeService.findForUser("elsa")).thenReturn(List.of(savedRecipe));

        mockMvc.perform(get("/api/recipes/saved")
                        .principal(UsernamePasswordAuthenticationToken.authenticated("elsa", null, List.of())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Tomato Soup"));
    }

    @Test
    void shouldReturnCreatedWhenSavingRecipe() throws Exception {
        ExternalRecipe recipe = new ExternalRecipe(
                "https://ica.se/soup", "Tomato Soup", "https://ica.se/soup.jpg", "ica.se");
        when(savedRecipeService.save("elsa", 4L)).thenReturn(new SavedRecipe(null, recipe));

        mockMvc.perform(post("/api/recipes/4/save")
                        .principal(UsernamePasswordAuthenticationToken.authenticated("elsa", null, List.of())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Tomato Soup"));
    }
}