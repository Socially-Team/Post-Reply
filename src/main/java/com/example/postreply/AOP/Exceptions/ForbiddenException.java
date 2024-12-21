package com.example.postreply.AOP.Exceptions;

// 403 Forbidden Exception
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
