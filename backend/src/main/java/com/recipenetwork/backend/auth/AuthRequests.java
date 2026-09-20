package com.recipenetwork.backend.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthRequests {

    private AuthRequests() {
    }

    public record Register(
            @NotBlank @Size(min = 3, max = 50) String username,
            @NotBlank @Email @Size(max = 255) String email,
            @NotBlank @Size(min = 8, max = 100) String password) {
    }

    public record Login(@NotBlank String username, @NotBlank String password) {
    }
}