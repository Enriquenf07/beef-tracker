package com.beeftracker.backend.viagens.controller;

import java.time.LocalDate;
import java.util.List;

import com.beeftracker.backend.base.Page;
import com.beeftracker.backend.base.exceptions.SensorIndisponivelException;
import com.beeftracker.backend.base.exceptions.UnauthorizedException;
import com.beeftracker.backend.usuarios.services.ValidateRoleService;
import com.beeftracker.backend.viagens.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.beeftracker.backend.base.exceptions.ResourceNotFoundException;
import com.beeftracker.backend.viagens.service.ViagemService;

@RestController
@RequestMapping("/viagem")
public class ViagemController {
    private final ViagemService service;
    private final ValidateRoleService validateRoleService;

    @Autowired
    public ViagemController(ViagemService service, ValidateRoleService validateRoleService) {
        this.service = service;
        this.validateRoleService = validateRoleService;
    }

    @PostMapping
    public ResponseEntity<?> criar(
            @RequestBody ViagemData data,
            @RequestAttribute("userId") String userId) throws ResourceNotFoundException, UnauthorizedException {
        validateRoleService.validate(Long.parseLong(userId), List.of("ADMIN", "VIAGEM"));
        service.criar(data);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<Page<Viagem>> pesquisar(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestAttribute("userId") String userId) throws UnauthorizedException {
        validateRoleService.validate(Long.parseLong(userId), List.of("ADMIN", "VIAGEM"));
        return ResponseEntity.ok(service.pesquisar(status, dataInicio, dataFim, page, Long.parseLong(userId)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> alterarStatus(
            @PathVariable Long id,
            @RequestBody NovoStatus status,
            @RequestAttribute("userId") String userId) throws ResourceNotFoundException, SensorIndisponivelException, UnauthorizedException {
        validateRoleService.validate(Long.parseLong(userId), List.of("ADMIN", "VIAGEM", "MOTORISTA"));
        service.alterarStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(
            @PathVariable Long id,
            @RequestBody ViagemData data,
            @RequestAttribute("userId") String userId) throws ResourceNotFoundException, UnauthorizedException {
        validateRoleService.validate(Long.parseLong(userId), List.of("ADMIN", "VIAGEM"));
        service.editar(id, data.descricao());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/leituras")
    public ResponseEntity<?> getLeituras(
            @PathVariable Long id,
            @RequestAttribute("userId") String userId) throws ResourceNotFoundException, UnauthorizedException {
        validateRoleService.validate(Long.parseLong(userId), List.of("ADMIN", "VIAGEM", "MOTORISTA"));
        return ResponseEntity.ok(service.getLeituras(id));
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<?> getStats(
            @PathVariable Long id,
            @RequestAttribute("userId") String userId) throws ResourceNotFoundException, UnauthorizedException {
        validateRoleService.validate(Long.parseLong(userId), List.of("ADMIN", "VIAGEM", "MOTORISTA"));
        return ResponseEntity.ok(service.getStats(id));
    }


    @GetMapping("/pendentes")
    public ResponseEntity<?> getStats(@RequestAttribute("userId") String userId) throws ResourceNotFoundException, UnauthorizedException {
        validateRoleService.validate(Long.parseLong(userId), List.of("ADMIN", "VIAGEM", "MOTORISTA"));
        return ResponseEntity.ok(service.listAllPendente());
    }
}