package com.example.ticketback.repository;

import java.time.LocalDateTime;

public interface DatesTicket {
    LocalDateTime getCreatedAt();
    LocalDateTime getClosedAt();
}
