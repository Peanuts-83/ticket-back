package com.example.ticketback.controller;

import com.example.ticketback.dto.TicketDto;
import com.example.ticketback.domain.Ticket;
import com.example.ticketback.domain.TicketStatus;
import com.example.ticketback.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private TicketService ticketService;

    @GetMapping
    public Page<TicketDto> list(@RequestParam TicketStatus status, Pageable pageable) {
        return ticketService.list(status, pageable);
    }

    @PostMapping
    public ResponseEntity<TicketDto> create(@RequestBody Ticket ticket) {
        return ResponseEntity.ok(ticketService.create(ticket));
    }

}
