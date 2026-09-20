package com.recipenetwork.backend.saved;

import com.recipenetwork.backend.auth.User;
import com.recipenetwork.backend.recipe.ExternalRecipe;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

@Entity
@Table(name = "saved_recipes", uniqueConstraints = @UniqueConstraint(
        name = "uk_saved_recipes_user_recipe", columnNames = {"user_id", "recipe_id"}))
public class SavedRecipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipe_id", nullable = false)
    private ExternalRecipe recipe;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected SavedRecipe() {
    }

    public SavedRecipe(User user, ExternalRecipe recipe) {
        this.user = user;
        this.recipe = recipe;
        this.createdAt = Instant.now();
    }

    public ExternalRecipe getRecipe() {
        return recipe;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}