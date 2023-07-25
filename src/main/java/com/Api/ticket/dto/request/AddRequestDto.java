package com.Api.ticket.dto.request;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.PrePersist;
import java.time.LocalDate;

@Data
@Component
public class AddRequestDto {

    private int clientId;

    private int ticketCode;

    private String title;

    private String status;

    private int pageNo;

    private int pageSize;

    private int id;
}
