package com.example.ticketback.security.auth.models;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Réponse positive à une demande de connexion")
public record LoginResponse(
        @Schema(
                description = "Token d'authentification renvoyé à chaque nouvelle requête. Doit être retourné dans les headers avec \"Bearer \" en préfixe",
                example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc3ODk0NzAyMywiZXhwIjoxNzc4OTQ4ODIzfQ.HWLqFYsOPlYFH2B-QEX-DWxIByJ-idAd_VzK6YXkvYQ"
        )
        String accessToken
) {
}
