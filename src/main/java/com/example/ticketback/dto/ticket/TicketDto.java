package com.example.ticketback.dto.ticket;

import com.example.ticketback.domain.entity.Ticket;
import com.example.ticketback.domain.enums.TicketStatus;
import com.example.ticketback.dto.common.MetaField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

@Getter @Setter
@AllArgsConstructor
public class TicketDto {
    @MetaField(libelle = "Id")
    private Long id;
    @MetaField(libelle = "Titre")
    private String title;
    @MetaField(libelle = "Description")
    private String description;
    @MetaField(libelle = "Statut")
    private TicketStatus status;
    @MetaField(libelle = "Date de création")
    private LocalDateTime createdAt;
    @MetaField(libelle = "Date de mise à jour")
    private LocalDateTime updatedAt;
    @MetaField(libelle = "Position")
    private int position;

    public static TicketDto fromEntity(Ticket ticket) {
        return new TicketDto(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt(),
                ticket.getPosition()
        );
    }
}
