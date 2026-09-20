package com.recipenetwork.backend.recipe;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@WebMvcTest(RecipeController.class)
@AutoConfigureMockMvc(addFilters = false)
class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecipeRepository recipeRepository;

    @MockBean
    private com.recipenetwork.backend.auth.UserRepository userRepository;

    @Test
    void shouldReturnRecipesFromRepository() throws Exception {
        when(recipeRepository.findAll()).thenReturn(List.of(
                new Recipe(1L, "Spaghetti Carbonara", "Creamy pasta with pancetta and parmesan.", 4.8)));

        mockMvc.perform(get("/api/recipes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Spaghetti Carbonara"))
                .andExpect(jsonPath("$[0].rating").value(4.8));
    }

    @Test
    void shouldCreateRecipe() throws Exception {
        Recipe savedRecipe = new Recipe(4L, "Tomato Soup", "A warm tomato soup.", 0.0);
        when(recipeRepository.save(any(Recipe.class))).thenReturn(savedRecipe);

        mockMvc.perform(post("/api/recipes")
                .contentType(APPLICATION_JSON)
                .content("{\"title\":\" Tomato Soup \",\"description\":\" A warm tomato soup. \"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.title").value("Tomato Soup"));
    }
}
