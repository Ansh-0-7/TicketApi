package com.Api.ticket.dto.response;

import lombok.Data;

@Data
public class ValidateResponse {
    private boolean valid;
    private String message;
}
