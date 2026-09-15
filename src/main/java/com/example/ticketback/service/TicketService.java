package com.example.ticketback.service;

import com.example.ticketback.dto.common.BaseHttpParams;
import com.example.ticketback.dto.ticket.PointJour;
import com.example.ticketback.dto.ticket.TicketDto;
import com.example.ticketback.domain.entity.Ticket;
import com.example.ticketback.domain.enums.TicketStatus;
import com.example.ticketback.dto.ticket.TicketFormDto;
import com.example.ticketback.dto.ticket.TicketStatsDto;
import com.example.ticketback.repository.DatesTicket;
import com.example.ticketback.repository.StatusCount;
import com.example.ticketback.repository.TicketRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class TicketService {
    private static final int NB_JOURS_STAT = 30;

    private final TicketRepository ticketRepository;

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public List<TicketDto> getList(@Nullable BaseHttpParams params) {
        Pageable pageable = params != null && params.paramList() != null ?
                PageRequest.of(
                        params.resolvedparamList().resolvedPageNum(),
                        params.resolvedparamList().resolvedNb()
                ) : Pageable.unpaged();

        Page<Ticket> ticketList = ticketRepository.findAll(pageable);
        return ticketList
                .stream()
                .map(TicketDto::fromEntity)
                .toList();
    }

    @PreAuthorize("isAuthenticated()")
    public TicketFormDto getMetaCreate() {
        return TicketFormDto.empty();
    }

    @PreAuthorize("isAuthenticated()")
    public TicketDto create(TicketFormDto ticket) {
        Ticket t = ticket.toEntity();
        // on positionne le ticket en bas de colonne
        t.setPosition(ticketRepository.getPositionMax(t.getStatus()) + 1);
        return TicketDto.fromEntity(ticketRepository.save(t));
    }

    @PreAuthorize("isAuthenticated()")
    public TicketDto update(TicketFormDto form) {
        Ticket t = ticketRepository.findById(form.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));
        TicketStatus status = t.getStatus();
        TicketStatus newStatus = form.status() != null ? form.status() : status;
        if (!status.equals(newStatus)) {
            t.setStatus(newStatus);
            // on compacte la colonne quittée
            reorder(status, null, null);
            // on insère le ticket à la position ou en bas de la nouvelle colonne
            reorder(newStatus, t, form.position());
        } else if  (form.position() != null) {
            // on a déplacé le ticket dans la même colonne
            reorder(status, t, form.position());
        }
        // pilotage closedAt
        if (newStatus.equals(TicketStatus.DONE) && !status.equals(TicketStatus.DONE)) {
            t.setClosedAt(LocalDateTime.now());
        } else if (!newStatus.equals(TicketStatus.DONE) && status.equals(TicketStatus.DONE)) {
            t.setClosedAt(null);
        }
        if (form.title() != null) {
            t.setTitle(form.title());
        }
        if (form.description() != null) {
            t.setDescription(form.description());
        }
        return TicketDto.fromEntity(t);
    }

    /**
     * Réécrit les positions d'une colonne, avec a_ticket & a_index null après un départ
     * @param a_status colonne/statut
     * @param a_ticket null pour un depart de la colonne
     * @param a_index
     */
    private void reorder(TicketStatus a_status, Ticket a_ticket, Integer a_index) {
        List<Ticket> l_colTickets = new ArrayList<>(ticketRepository.findByStatusOrderByPositionAscIdAsc(a_status));
        if (a_ticket != null) {
            l_colTickets.remove(a_ticket);
            int l_index = a_index != null ? Math.clamp(a_index, 0, l_colTickets.size()) : l_colTickets.size();
            l_colTickets.add(l_index,  a_ticket);
        }
        for (int i = 0; i < l_colTickets.size(); i++) {
            l_colTickets.get(i).setPosition(i);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public TicketStatsDto getTicketsStats() {
        long totalCount = 0L;
        long closedCount = 0L;
        long pendingCount = 0L;
        int progress = 0;
        List <StatusCount> l_nbByStatusList = ticketRepository.countByStatus();
        Map<TicketStatus, Long> byStatus = new EnumMap<>(TicketStatus.class);
        for (TicketStatus b_ticketStatus : TicketStatus.values()) {
            byStatus.put(b_ticketStatus, 0L);
        }
        for (StatusCount b_stat : l_nbByStatusList) {
            byStatus.put(b_stat.getStatus(), b_stat.getTotalCount() );
            totalCount += b_stat.getTotalCount();
            if (TicketStatus.DONE.equals(b_stat.getStatus())) {
                closedCount += b_stat.getTotalCount();
            }
        }
        pendingCount = totalCount - closedCount;
        progress = totalCount == 0 ? 0 : Math.round(((float) closedCount / totalCount) * 100f);
        List<PointJour> serie =  serieStatFactory();
        return new TicketStatsDto(
                totalCount,
                pendingCount,
                closedCount,
                progress,
                byStatus,
                serie);
    }

    /**
     * Calcul de la série de NB_JOUR_STAT
     */
    private List<PointJour> serieStatFactory() {
        LocalDate end =  LocalDate.now();
        LocalDate start = end.minusDays(NB_JOURS_STAT - 1);

        List<PointJour> l_result = new ArrayList<>();
        Map<LocalDate, long[]> l_map = new HashMap<>();
        List<DatesTicket> l_dates = ticketRepository.getTicketDatesFrom(start.atStartOfDay());
        for (DatesTicket b_dt : l_dates) {
            increment(l_map, b_dt.getCreatedAt(), start, 0);
            increment(l_map, b_dt.getClosedAt(), start, 1);
        }
        for (int i = 0; i < NB_JOURS_STAT; i++) {
            LocalDate b_day = start.plusDays(i);
            long[] b_counter = l_map.getOrDefault(b_day, new long[2]);
            l_result.add(new PointJour(b_day, b_counter[0], b_counter[1]));
        }
        return  l_result;
    }

    /**
     * Compteur createdAt/closedAt par date
     */
    private void increment(Map<LocalDate, long[]> a_map, LocalDateTime a_date, LocalDate a_start, int a_index) {
        if (a_date == null) {
            return;
        }
        LocalDate l_date = a_date.toLocalDate();
        if (l_date.isBefore(a_start)) {
            return;
        }
        a_map.computeIfAbsent(l_date, k -> new long[2])[a_index]++;
    }


}
