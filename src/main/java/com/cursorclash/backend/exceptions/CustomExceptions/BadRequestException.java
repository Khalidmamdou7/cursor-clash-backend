package com.cursorclash.backend.exceptions.CustomExceptions;


public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
