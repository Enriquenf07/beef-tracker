package com.beeftracker.backend.auth.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.beeftracker.backend.auth.models.user.UserData;
import com.beeftracker.backend.auth.services.JwtService;

@RestController
@RequestMapping("/public/auth")
public class AuthController {
    public final JwtService service;

    public AuthController(JwtService service){
        this.service = service;
    }
    @PostMapping
    public ResponseEntity<?> login(@RequestBody UserData user) throws Exception {
        return ResponseEntity.ok(new Token(service.gerarToken(user)));
    }
}

record Token(
    String token
){

}
