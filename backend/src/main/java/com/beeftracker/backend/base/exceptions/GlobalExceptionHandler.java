package com.beeftracker.backend.base.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException e) {
        ErrorBody body = new ErrorBody(null, "Item não existe", HttpStatus.BAD_REQUEST.value(), "Item não existe", "");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> handleResourceNotFound(UnauthorizedException e) {
        ErrorBody body = new ErrorBody(null, "Não autorizado", HttpStatus.BAD_REQUEST.value(), "Não autorizado", "");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception e) {
        ErrorBody body = new ErrorBody(null, "Erro Interno", HttpStatus.BAD_REQUEST.value(), "Erro Interno. Tente novamente mais tarde.", "");
        return ResponseEntity.badRequest()
                .body(body);
    }
}

record ErrorBody(
        String type,
        String title,
        Integer number,
        String detail,
        String instance
){

}
