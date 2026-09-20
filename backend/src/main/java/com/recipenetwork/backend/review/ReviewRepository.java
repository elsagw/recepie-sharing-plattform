package com.recipenetwork.backend.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByUserIdAndRecipeId(Long userId, Long recipeId);

    Optional<Review> findByUserIdAndRecipeId(Long userId, Long recipeId);

    Page<Review> findAllByOrderByCreatedAtDesc(Pageable pageable);
}