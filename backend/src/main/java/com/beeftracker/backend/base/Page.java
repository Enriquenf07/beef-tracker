package com.beeftracker.backend.base;

import java.util.List;

public record Page<T> (
        List<T> content,
        Integer pages

){

}
