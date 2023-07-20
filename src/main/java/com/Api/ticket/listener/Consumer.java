package com.Api.ticket.listener;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.Api.ticket.dao.TicketDaoImplementation;
import com.Api.ticket.model.TicketEntity;
import com.Api.ticket.service.TicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
public class Consumer {
    @Autowired
    TicketService ticketService;
    @Value("${spring.kafka.test.name}")
    public static final Logger logger= LoggerFactory.getLogger(Consumer.class);
    @KafkaListener(
            topics="${spring.kafka.test.name}",
            groupId="groupId",
            containerFactory = "userKafkaListenerFactory"
    )
    public void consumer(TicketEntity ticketEntity){
        logger.info("Consumed Successfully");
        ticketService.saveAddedTicket(ticketEntity);

    }

}