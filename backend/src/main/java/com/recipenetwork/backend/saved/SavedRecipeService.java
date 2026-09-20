package com.recipenetwork.backend.saved;

import com.recipenetwork.backend.auth.User;
import com.recipenetwork.backend.auth.UserRepository;
import com.recipenetwork.backend.recipe.ExternalRecipe;
import com.recipenetwork.backend.recipe.ExternalRecipeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SavedRecipeService {

    private final SavedRecipeRepository savedRecipeRepository;
    private final UserRepository userRepository;
    private final ExternalRecipeRepository recipeRepository;

    public SavedRecipeService(SavedRecipeRepository savedRecipeRepository, UserRepository userRepository,
                              ExternalRecipeRepository recipeRepository) {
        this.savedRecipeRepository = savedRecipeRepository;
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
    }

    public SavedRecipe save(String username, Long recipeId) {
        User user = findUser(username);
        ExternalRecipe recipe = findRecipe(recipeId);
        if (savedRecipeRepository.existsByUserIdAndRecipeId(user.getId(), recipeId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Recipe is already saved");
        }
        return savedRecipeRepository.save(new SavedRecipe(user, recipe));
    }

    public List<SavedRecipe> findForUser(String username) {
        return savedRecipeRepository.findAllByUserIdOrderByCreatedAtDesc(findUser(username).getId());
    }

    public void delete(String username, Long recipeId) {
        User user = findUser(username);
        SavedRecipe savedRecipe = savedRecipeRepository.findByUserIdAndRecipeId(user.getId(), recipeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Saved recipe not found"));
        savedRecipeRepository.delete(savedRecipe);
    }

    public boolean isSaved(String username, Long recipeId) {
        if (username == null) {
            return false;
        }
        return userRepository.findByUsername(username)
                .map(user -> savedRecipeRepository.existsByUserIdAndRecipeId(user.getId(), recipeId))
                .orElse(false);
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