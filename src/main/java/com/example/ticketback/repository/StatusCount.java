package com.example.ticketback.repository;

import com.example.ticketback.domain.enums.TicketStatus;

public interface StatusCount {
    TicketStatus getStatus();
    long getTotalCount();
}
