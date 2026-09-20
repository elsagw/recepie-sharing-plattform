package com.recipenetwork.backend.review;

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
@Table(name = "reviews", uniqueConstraints = @UniqueConstraint(name = "uk_reviews_user_recipe", columnNames = {"user_id", "recipe_id"}))
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipe_id", nullable = false)
    private ExternalRecipe recipe;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Review() {
    }

    public Review(User user, ExternalRecipe recipe, Integer rating, String comment) {
        this.user = user;
        this.recipe = recipe;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = Instant.now();
    }

    public User getUser() {
        return user;
    }

    public ExternalRecipe getRecipe() {
        return recipe;
    }

    public Integer getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void update(Integer rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }
}