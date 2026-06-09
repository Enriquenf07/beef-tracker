package com.beeftracker.backend.viagens.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beeftracker.backend.base.exceptions.ResourceNotFoundException;
import com.beeftracker.backend.viagens.service.ViagemService;
@RestController
@RequestMapping("/public/viagem")

public class ViagemPublicController {
    private final ViagemService service;

    @Autowired
    public ViagemPublicController(ViagemService service){
        this.service = service;
    }
    @GetMapping("/{token}/leituras")
    public ResponseEntity<?> getLeituras(@PathVariable String token) throws ResourceNotFoundException {
        Long id = service.getIdByToken(token);
        return ResponseEntity.ok(service.getLeituras(id));
    }

    @GetMapping("/{token}/stats")
    public ResponseEntity<?> getStats(@PathVariable String token) throws ResourceNotFoundException {
        Long id = service.getIdByToken(token);
        return ResponseEntity.ok(service.getStats(id));
    }

}
