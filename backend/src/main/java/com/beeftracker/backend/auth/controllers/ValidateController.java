package com.beeftracker.backend.auth.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth/validate")
public class ValidateController {
    @GetMapping
    public String teste(@RequestAttribute("userId") String userId) {
        return userId;
    }

}
