package com.example.ticketback.security.auth.models;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload de connection utilisateur")
public record LoginRequest(
        @Schema(
                description = "Nom utilisateur",
                example = "admin"
        )
        String username,
        @Schema(
                description = "Mot de pase utilisateur",
                example = "passWord?1"
        )
        String password
) {
}
