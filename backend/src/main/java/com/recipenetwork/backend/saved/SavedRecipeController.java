package com.recipenetwork.backend.saved;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}, allowCredentials = "true")
public class SavedRecipeController {

    private final SavedRecipeService savedRecipeService;

    public SavedRecipeController(SavedRecipeService savedRecipeService) {
        this.savedRecipeService = savedRecipeService;
    }

    @PostMapping("/{id}/save")
    @ResponseStatus(HttpStatus.CREATED)
    public SavedRecipeResponse save(@PathVariable Long id, Authentication authentication) {
        return SavedRecipeResponse.from(savedRecipeService.save(requireUsername(authentication), id));
    }

    @GetMapping("/saved")
    public List<SavedRecipeResponse> saved(Authentication authentication) {
        return savedRecipeService.findForUser(requireUsername(authentication)).stream()
                .map(SavedRecipeResponse::from)
                .toList();
    }

    @DeleteMapping("/{id}/save")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Authentication authentication) {
        savedRecipeService.delete(requireUsername(authentication), id);
    }

    private String requireUsername(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
        }
        return authentication.getName();
    }
}