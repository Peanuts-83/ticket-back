package com.example.ticketback.controller;

import com.example.ticketback.dto.common.BaseHttpParams;
import com.example.ticketback.dto.common.HttpPostPayload;
import com.example.ticketback.dto.common.HttpPostResult;
import com.example.ticketback.dto.ticket.TicketDto;
import com.example.ticketback.dto.ticket.TicketFormDto;
import com.example.ticketback.dto.ticket.TicketStatsDto;
import com.example.ticketback.service.TicketService;
import com.example.ticketback.web.ApiRoutes;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiRoutes.Ticket.BASE)
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping(ApiRoutes.Ticket.GET_LIST)
    public HttpPostResult<List<TicketDto>> getList(@RequestBody(required = false) BaseHttpParams params) {
        List<TicketDto> tickets = ticketService.getList(params);
        return HttpPostResult.ofList(tickets, (long) tickets.size());
    }

    @PostMapping(ApiRoutes.Ticket.META_CREATE)
    public HttpPostResult<TicketFormDto> metaCreate() {
        return HttpPostResult.ofMeta(ticketService.getMetaCreate());
    }

    @PostMapping(ApiRoutes.Ticket.CREATE)
    public HttpPostResult<TicketDto> create(@RequestBody HttpPostPayload<TicketFormDto> payload) {
        return HttpPostResult.of(ticketService.create(payload.data()));
    }

    @PostMapping(ApiRoutes.Ticket.UPDATE)
    public HttpPostResult<TicketDto> update(@RequestBody HttpPostPayload<TicketFormDto> payload) {
        return HttpPostResult.of(ticketService.update(payload.data()));
    }

    @PostMapping(ApiRoutes.Ticket.STATS)
    public HttpPostResult<TicketStatsDto> getStats() {
        return HttpPostResult.of(ticketService.getTicketsStats());
    }

}
