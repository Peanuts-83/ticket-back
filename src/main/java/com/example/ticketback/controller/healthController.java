package com.example.ticketback.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controleur technique de bonne santé de l'api
 */
@RestController
public class healthController {

    @GetMapping("/api/health")
    public String health() {
        return "TicketFlow back is running";
    }
}
