package com.Api.ticket.controller;

import com.Api.ticket.dto.request.AddRequestDto;
import com.Api.ticket.dto.response.UniformResponse;
import com.Api.ticket.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tickets/v1")

public class TicketController {
    @Autowired
    private TicketService ticketService;

    @PostMapping ("/get-all-tickets")
    public ResponseEntity<UniformResponse> getAllTickets(@RequestBody AddRequestDto addRequestDto) {
        return ticketService.getAllTickets(addRequestDto);
    }
    @GetMapping("/ticket-by-id")
    public ResponseEntity<UniformResponse>getTicketById(@RequestParam(name = "ticketId") int id) {
        return ticketService.getTicketById(id);

    }
    @GetMapping("/get-ticket-by-status")
    public ResponseEntity<UniformResponse> getTicketByStatus(@RequestParam(name = "status") String status){
                return ticketService.getTicketByStatus(status);
    }

    @PostMapping("/add-or-update-tickets")
    public ResponseEntity<UniformResponse> addOrUpdateTicket(@RequestBody AddRequestDto addRequestDto) {

        return ticketService.addOrUpdateTicket(addRequestDto);
    }
    @PostMapping("/add-or-update-tickets-by-kafka")
    public ResponseEntity<UniformResponse>addOrUpdateTicketByKafka(@RequestBody AddRequestDto addRequestDto){
        return ticketService.addOrUpdateTicketByKafka(addRequestDto);
    }


}
