package com.Api.ticket.utility;

import com.Api.ticket.dto.request.AddRequestDto;
import com.Api.ticket.dto.response.ValidateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Calendar;

public class Validator {
    private static final Logger logger = LoggerFactory.getLogger(Validator.class);

    public static Timestamp getDate() {
        Calendar calendar = Calendar.getInstance();
        return new Timestamp(calendar.getTimeInMillis());
    }
    public static ValidateResponse isValid(AddRequestDto addRequestDto){
        ValidateResponse validateResponse= new ValidateResponse();
        if(addRequestDto.getClientId()==0 || addRequestDto.getStatus()==null|| addRequestDto.getTitle()==null || addRequestDto.getTicketCode()==0){
            validateResponse.setValid(false);
            validateResponse.setMessage("Please specify all the required fields");
            return validateResponse;
        }
        else{
            if(Validator.isValidStatus(addRequestDto.getStatus()) && Validator.isValidTitle(addRequestDto.getTitle())){
                validateResponse.setValid(true);
                return validateResponse;
            }
            else{
                if(!Validator.isValidTitle(addRequestDto.getTitle())){
                    validateResponse.setValid(false);
                    validateResponse.setMessage("Enter the valid title up to length 30");
                } else if (!Validator.isValidStatus(addRequestDto.getStatus())) {
                    validateResponse.setMessage("Enter the valid status. It can be pending and complete only");
                    validateResponse.setValid(false);
                }
                return validateResponse;
            }
        }

    }
    public static boolean isValidStatus(String status){
        ValidateResponse validateResponse= new ValidateResponse();
        if(!status.equals("pending") && !status.equals("complete")){
            return false;
        }
        else{
          return true;
        }
    }
    public static boolean isValidTitle(String title){
        if(title.length()>30){
            return false;
        }
        return true;
    }
}

