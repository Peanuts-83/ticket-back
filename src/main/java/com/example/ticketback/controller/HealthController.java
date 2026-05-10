package com.example.ticketback.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controleur technique de bonne santé de l'api
 */
@RestController
public class HealthController {

    @GetMapping("/api/health")
    public Map<String, String> health() {
        return Map.of(
                "Status", "UP",
                "Application", "TicketFlow Back");
    }
}
