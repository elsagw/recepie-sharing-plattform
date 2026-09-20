package com.recipenetwork.backend.auth;

public record AuthResponse(Long id, String username, String email) {

    public static AuthResponse from(User user) {
        return new AuthResponse(user.getId(), user.getUsername(), user.getEmail());
    }
}