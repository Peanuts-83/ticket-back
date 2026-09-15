package com.example.ticketback.dto.ticket;

import com.example.ticketback.domain.enums.TicketStatus;
import com.example.ticketback.dto.common.MetaField;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

@Schema(description = "Stats des tickets")
public record TicketStatsDto(
        @Schema(description = "Nb total de tickets")
        @MetaField(libelle = "Total")
        long total,
        @Schema(description = "NEW | CONCEPTION | ACTIVE | REVIEW")
        @MetaField(libelle = "Non clos")
        long pending,
        @Schema(description = "DONE")
        @MetaField(libelle = "Clos")
        long closed,
        @Schema(description = "% d'avancement")
        @MetaField(libelle = "Progression")
        int progress,
        @Schema(description = "Nb de tickets par colonne")
        @MetaField(libelle = "Par statut")
        Map<TicketStatus, Long> byStatus,
        @Schema(description = "Liste sur 30 jours")
        @MetaField(libelle = "30 derniers jours")
        List<PointJour> serie
) {
//    public static TicketStatsDto fromEntity(TicketStats stat) {
//        return new TicketStatsDto(
//
//
//        );
//    }
}

