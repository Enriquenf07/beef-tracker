package com.beeftracker.backend.auth.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<?> login(@RequestBody UserData user){
        try{
            return ResponseEntity.ok(new Token(service.gerarToken(user)));
        }catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }
}

record Token(
    String token
){

}
