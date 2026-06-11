package com.beeftracker.backend.veiculos.controllers;

import com.beeftracker.backend.base.exceptions.InvalidFormException;
import com.beeftracker.backend.base.exceptions.ResourceNotFoundException;
import com.beeftracker.backend.base.exceptions.UnauthorizedException;
import com.beeftracker.backend.usuarios.services.ValidateRoleService;
import com.beeftracker.backend.veiculos.models.VeiculoData;
import com.beeftracker.backend.veiculos.services.VeiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/veiculos")
public class VeiculoController {

    @Autowired
    private VeiculoService veiculoService;

    @Autowired
    private ValidateRoleService roleService;

    @GetMapping
    public ResponseEntity<?> listar(
            @RequestAttribute("userId") String userId,
            @RequestParam(required = false) String chave,
            @RequestParam(required = false) Boolean status)
            throws UnauthorizedException {
        roleService.validate(Long.parseLong(userId), List.of("ADMIN", "GESTAO"));
        return ResponseEntity.ok(veiculoService.pesquisar(chave, status));
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(
            @RequestAttribute("userId") String userId,
            @RequestBody VeiculoData data)
            throws InvalidFormException, UnauthorizedException {
        roleService.validate(Long.parseLong(userId), List.of("ADMIN", "GESTAO"));
        veiculoService.salvar(data);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(
            @RequestAttribute("userId") String userId,
            @PathVariable Long id,
            @RequestBody VeiculoData data)
            throws InvalidFormException, ResourceNotFoundException, UnauthorizedException {
        roleService.validate(Long.parseLong(userId), List.of("ADMIN", "GESTAO"));
        veiculoService.editar(id, data);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> alterarStatus(
            @RequestAttribute("userId") String userId,
            @PathVariable Long id)
            throws ResourceNotFoundException, UnauthorizedException {
        roleService.validate(Long.parseLong(userId), List.of("ADMIN", "GESTAO"));
        veiculoService.alterarStatus(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(
            @RequestAttribute("userId") String userId,
            @PathVariable Long id)
            throws ResourceNotFoundException, UnauthorizedException {
        roleService.validate(Long.parseLong(userId), List.of("ADMIN", "GESTAO"));
        veiculoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}