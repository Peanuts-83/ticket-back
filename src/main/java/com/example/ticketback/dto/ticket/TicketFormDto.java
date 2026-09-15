package com.example.ticketback.dto.ticket;

import com.example.ticketback.domain.entity.Ticket;
import com.example.ticketback.domain.enums.TicketStatus;
import com.example.ticketback.dto.common.MetaField;

public record TicketFormDto(
        @MetaField(libelle = "Id")
        Long id,
        @MetaField(libelle = "Titre")
        String title,
        @MetaField(libelle = "Description")
        String description,
        @MetaField(libelle = "Statut")
        TicketStatus status,
        @MetaField(libelle = "Position")
        Integer position
) {
    public static TicketFormDto fromEntity(Ticket ticket) {
        return new TicketFormDto(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getPosition()
        );
    }

    public Ticket toEntity() {
        Ticket result = new Ticket();
        result.setTitle(title);
        result.setDescription(description);
        result.setStatus(status);
        result.setPosition(position);
        return result;
    }

    public static TicketFormDto empty() {
        return new TicketFormDto(null,null,null,null,null);
    }
}
