package com.beeftracker.backend.base.exceptions;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends Exception {
    private final ErrorBody body;
    public ErrorBody getBody() {
        return body;
    }
    public UnauthorizedException() {
        super();
        body = new ErrorBody(null, "Não autorizado", HttpStatus.BAD_REQUEST.value(), "Não autorizado", "");

    }
}
