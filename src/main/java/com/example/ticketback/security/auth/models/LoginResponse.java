package com.example.ticketback.security.auth.models;

import com.example.ticketback.domain.entity.User;
import com.example.ticketback.dto.user.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Réponse positive à une demande de connexion")
public record LoginResponse(
        @Schema(
                description = "Token d'authentification renvoyé à chaque nouvelle requête. Doit être retourné dans les headers avec \"Bearer \" en préfixe"
        )
        String accessToken,

        @Schema(
                description = "Token de refresh, qui gère le temps de connexion autorisé"
        )
        String refreshToken,

        @Schema(
                description = "Utilisateur"
        )
        UserDto user
) { }
