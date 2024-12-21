package com.example.postreply.AOP.Exceptions;

// 400 Bad Request Exception
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
