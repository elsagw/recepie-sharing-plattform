package com.recipenetwork.backend.recipe;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecipeScrapeController.class)
@AutoConfigureMockMvc(addFilters = false)
class RecipeScrapeControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private RecipeScraper recipeScraper;

  @MockBean
  private com.recipenetwork.backend.auth.UserRepository userRepository;

  @Test
  void shouldReturnScrapedRecipePreview() throws Exception {
    when(recipeScraper.scrape("https://ica.se/recept/soup"))
        .thenReturn(new ExternalRecipe(
            "https://ica.se/recept/soup",
            "Tomato Soup",
            "https://ica.se/soup.jpg",
            "ica.se"));

    mockMvc.perform(post("/api/recipes/scrape")
        .param("url", "https://ica.se/recept/soup"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.sourceUrl").value("https://ica.se/recept/soup"))
        .andExpect(jsonPath("$.title").value("Tomato Soup"))
        .andExpect(jsonPath("$.domain").value("ica.se"));
  }
}