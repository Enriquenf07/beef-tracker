package com.beeftracker.backend.base.exceptions;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends Exception {
    private final ErrorBody body;
    public ErrorBody getBody() {
        return body;
    }
    public ResourceNotFoundException() {
        super();
        body = new ErrorBody(null, "Item não existe", HttpStatus.BAD_REQUEST.value(), "Item não existe", "");

    }
}
