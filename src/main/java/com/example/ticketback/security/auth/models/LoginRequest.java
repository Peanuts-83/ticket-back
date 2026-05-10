package com.example.ticketback.security.auth.models;

public record LoginRequest(
        String username,
        String password
) {
}
