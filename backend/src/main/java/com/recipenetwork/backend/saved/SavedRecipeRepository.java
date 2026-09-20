package com.recipenetwork.backend.saved;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedRecipeRepository extends JpaRepository<SavedRecipe, Long> {

    boolean existsByUserIdAndRecipeId(Long userId, Long recipeId);

    Optional<SavedRecipe> findByUserIdAndRecipeId(Long userId, Long recipeId);

    List<SavedRecipe> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}