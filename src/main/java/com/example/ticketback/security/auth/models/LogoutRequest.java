package com.example.ticketback.security.auth.models;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload de déconnexion")
public record LogoutRequest(
        @Schema(
                description = "RefreshToken"
        )
        String refreshToken
) {
}
