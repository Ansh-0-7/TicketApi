package com.Api.ticket.controller;

import com.Api.ticket.dto.request.AddRequestDto;
import com.Api.ticket.dto.response.UniformResponse;
import com.Api.ticket.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@RestController
@RequestMapping("/tickets/v1")

public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping("/get-all-tickets")
    public ResponseEntity<UniformResponse> getAllTickets(@RequestBody AddRequestDto addRequestDto) {
        return ticketService.getAllTickets(addRequestDto);
    }

    @GetMapping("/ticket-by-id")
    public ResponseEntity<UniformResponse> getTicketById(@RequestBody AddRequestDto addRequestDto) {
        return ticketService.getTicketById(addRequestDto);

    }

    @GetMapping("/get-ticket-by-status")
    public ResponseEntity<UniformResponse> getTicketByStatus(@RequestBody AddRequestDto addRequestDto) {
        return ticketService.getTicketByStatus(addRequestDto);
    }

    @PostMapping("/add-or-update-tickets")
    public ResponseEntity<UniformResponse> addOrUpdateTicket(@RequestBody AddRequestDto addRequestDto) {

        return ticketService.addOrUpdateTicket(addRequestDto);
    }

    @PostMapping("/add-or-update-tickets-by-kafka")
    public ResponseEntity<UniformResponse> addOrUpdateTicketByKafka(@RequestBody AddRequestDto addRequestDto) {
        return ticketService.addOrUpdateTicketByKafka(addRequestDto);
    }

    @GetMapping("get-all-tickets-by-rest-template")
    public ResponseEntity<UniformResponse> getAllTicketsByRestTemplate(@RequestBody AddRequestDto addRequestDto) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8083/tickets/v1/get-all-tickets";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AddRequestDto> requestEntity = new HttpEntity<>(addRequestDto, headers);
        ResponseEntity<UniformResponse> userResponseDto = restTemplate.exchange(url, HttpMethod.POST, requestEntity, UniformResponse.class);
        return userResponseDto;
    }

    @GetMapping("add-or-update-tickets-by-rest")
    public ResponseEntity<UniformResponse> addOrUpdateTicketsByRest(@RequestBody AddRequestDto addRequestDto) {
        RestTemplate restTemplate=new RestTemplate();
        String url="http://localhost:8083/tickets/v1/add-or-update-tickets-by-kafka";
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AddRequestDto> requestEntity=new HttpEntity<>(addRequestDto,headers);
        ResponseEntity<UniformResponse>uniformResponseDto=restTemplate.exchange(url,HttpMethod.POST,requestEntity,UniformResponse.class);
        return uniformResponseDto;
    }

}


