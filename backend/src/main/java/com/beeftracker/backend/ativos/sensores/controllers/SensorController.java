package com.beeftracker.backend.ativos.sensores.controllers;

import com.beeftracker.backend.base.exceptions.UnauthorizedException;
import com.beeftracker.backend.usuarios.services.ValidateRoleService;
import org.springframework.web.bind.annotation.*;

import com.beeftracker.backend.ativos.sensores.models.SensorData;
import com.beeftracker.backend.ativos.sensores.services.SensorService;
import com.beeftracker.backend.base.exceptions.ResourceNotFoundException;

import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/sensor")
public class SensorController {

    private final SensorService service;
    private final ValidateRoleService roleService;

    public SensorController(SensorService service, ValidateRoleService roleService) {
        this.service = service;
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<?> pesquisar(
            @RequestAttribute("userId") String userId,
            @RequestParam(required = false) String chave,
            @RequestParam(required = false) Boolean status)
            throws UnauthorizedException {
        roleService.validate(Long.parseLong(userId), List.of("ADMIN", "GESTAO"));
        return ResponseEntity.ok(service.pesquisar(chave, status));
    }

    @GetMapping("/all")
    public ResponseEntity<?> listAll(
            @RequestAttribute("userId") String userId)
            throws UnauthorizedException {
        roleService.validate(Long.parseLong(userId), List.of("ADMIN", "GESTAO"));
        return ResponseEntity.ok(service.listAll());
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(
            @RequestAttribute("userId") String userId,
            @RequestBody SensorData data)
            throws UnauthorizedException {
        roleService.validate(Long.parseLong(userId), List.of("ADMIN", "GESTAO"));
        service.cadastrar(data);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(
            @RequestAttribute("userId") String userId,
            @PathVariable Long id,
            @RequestBody SensorData data)
            throws ResourceNotFoundException, UnauthorizedException {
        roleService.validate(Long.parseLong(userId), List.of("ADMIN", "GESTAO"));
        service.editar(id, data);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> editarStatus(
            @RequestAttribute("userId") String userId,
            @PathVariable Long id)
            throws ResourceNotFoundException, UnauthorizedException {
        roleService.validate(Long.parseLong(userId), List.of("ADMIN", "GESTAO"));
        service.editarStatus(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> carregar(
            @RequestAttribute("userId") String userId,
            @PathVariable Long id)
            throws ResourceNotFoundException, UnauthorizedException {
        roleService.validate(Long.parseLong(userId), List.of("ADMIN", "GESTAO"));
        return ResponseEntity.ok(service.carregar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(
            @RequestAttribute("userId") String userId,
            @PathVariable Long id)
            throws ResourceNotFoundException, UnauthorizedException {
        roleService.validate(Long.parseLong(userId), List.of("ADMIN", "GESTAO"));
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}