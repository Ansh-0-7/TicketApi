package com.Api.ticket.service;

import com.Api.ticket.cache.TicketCache;
import com.Api.ticket.dao.TicketDaoImplementation;
import com.Api.ticket.dto.request.AddRequestDto;
import com.Api.ticket.dto.response.UniformResponse;
import com.Api.ticket.dto.response.ValidateResponse;
import com.Api.ticket.model.TicketEntity;
import com.Api.ticket.utility.Validator;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service

public class TicketService {
    @Autowired
    TicketCache ticketCache;
    @Autowired
    private TicketDaoImplementation ticketDaoImplementation;
    final org.slf4j.Logger logger = LoggerFactory.getLogger(TicketDaoImplementation.class);
    @Value("${spring.kafka.test.name}")
    String topicName;

    @Autowired
    private KafkaTemplate<String,TicketEntity> kafkaTemplate;

    public ResponseEntity<UniformResponse> getAllTickets(AddRequestDto dto) {
        List<TicketEntity> ticketData=new ArrayList<>();

        UniformResponse responseListDto = new UniformResponse();
        try {

            if (dto.getPageNo() <= 0 || dto.getPageNo() <= 0) {
                responseListDto.setStatus(false);
                responseListDto.setData(null);
                responseListDto.setMessage("Failed");
                return new ResponseEntity<>(responseListDto, HttpStatus.CONFLICT);
            }
            ticketData=ticketCache.getAllTickets(dto.getPageNo());
            if(ticketData.size()!=0){
                responseListDto.setStatus(true);
                responseListDto.setMessage("Fetched from Cache");
                responseListDto.setData(ticketData);
                return new ResponseEntity<>(responseListDto,HttpStatus.ACCEPTED);
            }
            else {
                List<TicketEntity> values = ticketDaoImplementation.getAllTickets(dto.getPageNo(), dto.getPageSize());
                if (values != null && values.size() > 0) {
                    responseListDto.setMessage("Found all the tickets by database");
                    responseListDto.setStatus(true);
                    responseListDto.setData(values);
                    ticketCache.ticketListToCache(dto.getPageNo(), values);
                    return new ResponseEntity<>(responseListDto, HttpStatus.ACCEPTED);
                } else {
                    responseListDto.setMessage("Empty Record");
                    responseListDto.setStatus(false);
                    responseListDto.setData(null);
                    return new ResponseEntity<>(responseListDto, HttpStatus.BAD_GATEWAY);
                }
            }
        } catch (Exception e) {
            responseListDto.setStatus(false);
            responseListDto.setData(null);
            responseListDto.setMessage("Failed");
            logger.error("Tickets not fetched.Invalid Input for pageNo and PageSize");
            return new ResponseEntity<>(responseListDto, HttpStatus.BAD_REQUEST);
        }

    }

    public ResponseEntity<UniformResponse> getTicketById(int id) {

        UniformResponse uniformResponse= new UniformResponse();
       TicketEntity ticketEntity=ticketCache.getTicketByTicketId(id);
        if(ticketEntity!=null){
            uniformResponse.setData(ticketEntity);
            uniformResponse.setStatus(true);
            uniformResponse.setMessage("Fetched by cache");
        }
        else{
            ticketEntity= ticketDaoImplementation.getTicketById(id);
            if(ticketEntity==null){
                uniformResponse.setMessage("Invalid id");
                uniformResponse.setStatus(false);
                uniformResponse.setData(null);
                logger.error("Invalid id");
                return new ResponseEntity<>(uniformResponse,HttpStatus.CONFLICT);
            }
           uniformResponse.setData(ticketEntity);
            uniformResponse.setStatus(true);
            uniformResponse.setMessage("Fetched from database");
            ticketCache.putTicketToTicketIdTicketMap(ticketEntity);
        }
        return new ResponseEntity<>(uniformResponse,HttpStatus.ACCEPTED);
    }

    @Transactional
    public ResponseEntity<UniformResponse> addOrUpdateTicket(AddRequestDto addRequestDto) {
        UniformResponse uniformResponse = new UniformResponse();
        try {
            Optional<TicketEntity> ticketEntityOptional = ticketDaoImplementation.getTicketByCode(addRequestDto.getTicketCode());
            TicketEntity ticket;
            if (ticketEntityOptional.isPresent()) {
                ValidateResponse validateResponse = Validator.isValid(addRequestDto);
                if (validateResponse.isValid()) {
                    ticket = ticketEntityOptional.get();
                    ticket.setClientId(addRequestDto.getClientId());
                    ticket.setStatus(addRequestDto.getStatus());
                    ticket.setTitle(addRequestDto.getTitle());
                    ticket.setLastModifiedDate(Validator.getDate());
                    uniformResponse.setMessage("Updated Successfully");
                    uniformResponse.setData(ticket);
                    uniformResponse.setStatus(true);
                } else {
                    uniformResponse.setMessage(validateResponse.getMessage());
                    uniformResponse.setData(null);
                    uniformResponse.setStatus(false);
                    return new ResponseEntity<>(uniformResponse, HttpStatus.BAD_REQUEST);
                }
            } else {
                // Ticket doesn't exist, create a new one
                ValidateResponse validateResponse = Validator.isValid(addRequestDto);
                if (validateResponse.isValid()) {
                    ticket = new TicketEntity();
                    ticket.setTicketCode(addRequestDto.getTicketCode());
                    ticket.setClientId(addRequestDto.getClientId());
                    ticket.setTitle(addRequestDto.getTitle());
                    ticket.setStatus(addRequestDto.getStatus());
                    ticket.setLastModifiedDate(Validator.getDate());
                } else {
                    uniformResponse.setMessage(validateResponse.getMessage());
                    uniformResponse.setData(null);
                    uniformResponse.setStatus(false);
                    return new ResponseEntity<>(uniformResponse, HttpStatus.BAD_REQUEST);
                }
            }
            ticketDaoImplementation.addTicket(ticket);
            return new ResponseEntity<>(uniformResponse, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            uniformResponse.setMessage("Failed");
            uniformResponse.setStatus(false);
            logger.error("Exception:" + e);
            return new ResponseEntity<>(uniformResponse, HttpStatus.BAD_REQUEST);
        }
    }


    public ResponseEntity<UniformResponse> getTicketByStatus(String status) {
        UniformResponse responseListDto = new UniformResponse();
        List<TicketEntity> ticketEntity = ticketDaoImplementation.getTicketByStatus(status);
        if (ticketEntity == null) {
            responseListDto.setMessage("Invalid Status");
            responseListDto.setStatus(false);
            responseListDto.setData(null);
            return new ResponseEntity<>(responseListDto, HttpStatus.CONFLICT);
        } else {
            if (Validator.isValidStatus(status)) {
                responseListDto.setMessage("Success");
                responseListDto.setStatus(true);
                responseListDto.setData(ticketEntity);
                return new ResponseEntity<>(responseListDto, HttpStatus.ACCEPTED);
            } else {
                responseListDto.setMessage("Enter a valid status pending or complete.");
                responseListDto.setStatus(false);
                responseListDto.setData(ticketEntity);
                return new ResponseEntity<>(responseListDto, HttpStatus.BAD_REQUEST);
            }
        }
    }

    public ResponseEntity<UniformResponse> addOrUpdateTicketByKafka(AddRequestDto addRequestDto) {
        UniformResponse uniformResponse = new UniformResponse();
        try {
            Optional<TicketEntity> ticketEntityOptional = ticketDaoImplementation.getTicketByCode(addRequestDto.getTicketCode());
            TicketEntity ticket;
            if (ticketEntityOptional.isPresent()) {
                ValidateResponse validateResponse = Validator.isValid(addRequestDto);
                if (validateResponse.isValid()) {
                    ticket = ticketEntityOptional.get();
                    ticket.setClientId(addRequestDto.getClientId());
                    ticket.setStatus(addRequestDto.getStatus());
                    ticket.setTitle(addRequestDto.getTitle());
                    ticket.setLastModifiedDate(Validator.getDate());
                    kafkaTemplate.send("TICKET_TOPIC",ticket);
                    uniformResponse.setMessage("Updated Successfully");
                    uniformResponse.setData(ticket);
                    uniformResponse.setStatus(true);
                } else {
                    uniformResponse.setMessage(validateResponse.getMessage());
                    uniformResponse.setData(null);
                    uniformResponse.setStatus(false);
                    return new ResponseEntity<>(uniformResponse, HttpStatus.BAD_REQUEST);
                }
            } else {
                // Ticket doesn't exist, create a new one
                ValidateResponse validateResponse = Validator.isValid(addRequestDto);
                if (validateResponse.isValid()) {
                    ticket = new TicketEntity();
                    ticket.setTicketCode(addRequestDto.getTicketCode());
                    ticket.setClientId(addRequestDto.getClientId());
                    ticket.setTitle(addRequestDto.getTitle());
                    ticket.setStatus(addRequestDto.getStatus());
                    ticket.setLastModifiedDate(Validator.getDate());
                    kafkaTemplate.send(topicName,ticket);
                } else {
                    uniformResponse.setMessage(validateResponse.getMessage());
                    uniformResponse.setData(null);
                    uniformResponse.setStatus(false);
                    return new ResponseEntity<>(uniformResponse, HttpStatus.BAD_REQUEST);
                }
            }
            return new ResponseEntity<>(uniformResponse, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            uniformResponse.setMessage("Failed");
            uniformResponse.setStatus(false);
            logger.error("Exception:" + e);
            return new ResponseEntity<>(uniformResponse, HttpStatus.BAD_REQUEST);
        }
    }

    public void saveAddedTicket(TicketEntity ticketEntity) {

        try {
            ticketDaoImplementation.addTicket(ticketEntity);
        }
        catch (Exception e){
            logger.error(e.getMessage());
            return;
        }
    }

}