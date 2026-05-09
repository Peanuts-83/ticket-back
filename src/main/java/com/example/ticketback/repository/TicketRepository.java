package com.example.ticketback.repository;

import com.example.ticketback.domain.enums.TicketStatus;
import com.example.ticketback.domain.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Page<Ticket> findByStatus(TicketStatus status, Pageable pageable);
}
