package com.example.postreply.AOP;

import com.example.postreply.AOP.Exceptions.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> illegalArgumentExceptionHandler(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> dataIntegrityViolationExceptionHandler(DataIntegrityViolationException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    // 403 Forbidden
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<String> handleForbiddenException(ForbiddenException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    // 400 Bad Request
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequestException(BadRequestException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // 404 Not Found
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // others
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        return new ResponseEntity<>("An error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

