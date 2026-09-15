package com.example.ticketback.dto.ticket;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Stats tickets sur une journée")
public record PointJour (
        LocalDate date,
        long created,
        long closed
) {}
