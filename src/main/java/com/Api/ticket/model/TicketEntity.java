package com.Api.ticket.model;

import com.Api.ticket.utility.Validator;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "ticket")

public class TicketEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "client_id")
    private int clientId;
    @Column(name = "ticket_code")
    private int ticketCode;
    @Column(name = "title")
    private String title;
    @Column(name= "last_modified_date")
    private Timestamp lastModifiedDate;
    @PrePersist
    public void setDefault(){
        this.setLastModifiedDate(Validator.getDate());
    }
    @Column(name = "status")
    private String status;
}
