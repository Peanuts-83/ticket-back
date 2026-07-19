package com.example.ticketback.security.auth.models;

import com.example.ticketback.dto.user.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Réponse positive à une demande de déconnexion")
public record LogoutResponse(
        @Schema(
                description = "Message de confirmation"
        )
        String msg
) { }
