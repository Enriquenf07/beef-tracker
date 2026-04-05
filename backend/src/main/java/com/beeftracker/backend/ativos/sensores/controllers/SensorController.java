package com.beeftracker.backend.ativos.sensores.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beeftracker.backend.ativos.sensores.models.SensorData;
import com.beeftracker.backend.ativos.sensores.services.SensorService;
import com.beeftracker.backend.base.exceptions.ResourceNotFoundException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/sensor")
public class SensorController {
    private final SensorService service;

    public SensorController(SensorService service) {
        this.service = service;
    }

    @GetMapping("path")
    public String getMethodName(@RequestParam String param) {
        return new String();
    }
    
    public ResponseEntity<?> pesquisar(
            @RequestParam(required = false) String chave,
            @RequestParam(required = false) Boolean status) {
        return ResponseEntity.ok(service.pesquisar(chave, status));
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody SensorData data) {
        service.cadastrar(data);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody SensorData data) throws ResourceNotFoundException {
        service.editar(id, data);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> editarStatus(@PathVariable Long id) throws ResourceNotFoundException {
        service.editarStatus(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> carregar(@PathVariable Long id) throws ResourceNotFoundException {
        return ResponseEntity.ok(service.carregar(id));
    }
}
