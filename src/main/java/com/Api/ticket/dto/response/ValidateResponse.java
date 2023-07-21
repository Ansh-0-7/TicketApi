package com.Api.ticket.dto.response;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ValidateResponse {
  boolean valid;
  String message;
}
