package com.Api.ticket.dao;

import com.Api.ticket.model.TicketEntity;

import java.util.List;
import java.util.Optional;

public interface TicketDao {
    public List<TicketEntity> getAllTickets();
    public TicketEntity getTicketById(int id);
    public void addTicket(TicketEntity ticketEntity);
    Optional<TicketEntity> getTicketByCode(int ticketCode);
    public List<TicketEntity> getTicketByStatus(String status);

}
