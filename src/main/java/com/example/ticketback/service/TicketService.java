package com.example.ticketback.service;

import com.example.ticketback.dto.TicketDto;
import com.example.ticketback.domain.Ticket;
import com.example.ticketback.domain.TicketStatus;
import com.example.ticketback.repository.TicketRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    public Page<TicketDto> list(TicketStatus status, Pageable pageable) {
        return ticketRepository
                .findByStatus(status, pageable)
                .map(TicketDto::fromEntity);
    }

    public TicketDto create(Ticket ticket) {
        return TicketDto.fromEntity(ticketRepository.save(ticket));
    }

}
