package com.example.ticketback.repository;

import com.example.ticketback.domain.enums.TicketStatus;
import com.example.ticketback.domain.entity.Ticket;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface TicketRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {
    Page<Ticket> findByStatus(@Nullable TicketStatus status, Pageable pageable);

    List<Ticket> findByClosedAtIsNotNull();

    List<Ticket> findByClosedAtIsNull();

    @Query("select t.status as status, count(t) as totalCount from Ticket t group by t.status")
    List<StatusCount> countByStatus();

    @Query("select coalesce(max(t.position), -1) from Ticket t where t.status = :status")
    int getPositionMax(@Param("status")  TicketStatus status);

    /** Couples (createdAt, closedAt) agrégés par jour */
    @Query("select t.createdAt as createdAt, t.closedAt as closedAt from Ticket t "
        +"where t.createdAt >= :date or t.closedAt >= :date")
    List<DatesTicket> getTicketDatesFrom(@Param("date") LocalDateTime date);

    List<Ticket> findByStatusOrderByPositionAscIdAsc(TicketStatus status);
}

