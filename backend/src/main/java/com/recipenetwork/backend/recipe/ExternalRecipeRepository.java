package com.recipenetwork.backend.recipe;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExternalRecipeRepository extends JpaRepository<ExternalRecipe, Long> {

    Optional<ExternalRecipe> findBySourceUrl(String sourceUrl);
}