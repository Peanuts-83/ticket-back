package com.example.ticketback.dto;

import com.example.ticketback.domain.Ticket;
import com.example.ticketback.domain.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TicketDto {

    private Long id;
    private String title;
    private String description;
    private TicketStatus status;

    public static TicketDto fromEntity(Ticket ticket) {
        return new TicketDto(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getStatus()
        );
    }
}
