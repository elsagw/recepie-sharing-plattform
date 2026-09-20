package com.recipenetwork.backend.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateReviewRequest(
        @NotNull @Min(1) @Max(10) Integer rating,
        @NotBlank @Size(max = 2000) String comment) {
}