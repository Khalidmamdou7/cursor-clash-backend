package com.cursorclash.backend.exceptions;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Data
@Getter
@Setter

public class ErrorDetails {

    private Date timestamp;
    private int httpStatus;
    private String message;
    private String details;

    public ErrorDetails(int httpStatus, String message, String details) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.details = details;
        this.timestamp = new Date();
    }
}
