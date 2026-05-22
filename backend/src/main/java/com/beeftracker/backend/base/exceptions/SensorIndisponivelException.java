package com.beeftracker.backend.base.exceptions;

import org.springframework.http.HttpStatus;

public class SensorIndisponivelException extends Exception {
    private final ErrorBody body;
    public ErrorBody getBody() {
        return body;
    }
    public SensorIndisponivelException() {
        super();
        body = new ErrorBody(null, "Sensor indisponível", HttpStatus.BAD_REQUEST.value(), "Já existe uma viagem ativa com esse sensor", "");

    }
}

