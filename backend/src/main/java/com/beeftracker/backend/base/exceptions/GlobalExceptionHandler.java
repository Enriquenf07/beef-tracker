package com.beeftracker.backend.base.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getBody());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> handleUnauthorizer(UnauthorizedException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getBody());
    }

    @ExceptionHandler(InvalidFormException.class)
    public ResponseEntity<?> handleInvalidForm(InvalidFormException e) {
        return ResponseEntity.badRequest().body(e.getBody());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception e) {
        ErrorBody body = new ErrorBody(null, "Erro Interno", HttpStatus.BAD_REQUEST.value(), "Erro Interno. Tente novamente mais tarde.", "");
        return ResponseEntity.badRequest()
                .body(body);
    }
}


