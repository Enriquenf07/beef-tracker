package com.beeftracker.backend.base.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidFormException extends Exception {
    private final ErrorBody errorBody;
    public ErrorBody getBody() {
        return errorBody;
    }

    public InvalidFormException(String msg) {
        super();
        errorBody = new ErrorBody(null, "Formulário inválido", HttpStatus.BAD_REQUEST.value(), "Formulário inválido: " + msg, "");
    }

    public InvalidFormException() {
        super();
        errorBody = new ErrorBody(null, "Formulário inválido", HttpStatus.BAD_REQUEST.value(), "Formulário inválido", "");
    }
}
