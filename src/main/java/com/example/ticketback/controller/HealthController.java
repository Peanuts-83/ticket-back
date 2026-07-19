package com.example.ticketback.controller;

import com.example.ticketback.web.ApiRoutes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controleur technique de bonne santé de l'api
 */
@RestController
@Tag(
        name = "Statut de l'api",
        description = "Endpoint de contrôle de bonne santé."
)
public class HealthController {

    @GetMapping(ApiRoutes.Health.HEALTH)
    @PreAuthorize("permitAll")
    @SecurityRequirements
    @Operation(
            summary = "[PUBLIC] Confirme le bon fonctionnement du back-end",
            tags = {"PUBLIC"},
            description = "Retourne le statut et le nom de l'application"
    )
    public Map<String, String> health() {
        return Map.of(
                "Status", "UP",
                "Application", "TicketFlow Back");
    }
}
