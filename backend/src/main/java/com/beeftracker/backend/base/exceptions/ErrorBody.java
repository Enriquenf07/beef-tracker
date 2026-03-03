package com.beeftracker.backend.base.exceptions;

public record ErrorBody(
        String type,
        String title,
        Integer number,
        String detail,
        String instance
){

}
