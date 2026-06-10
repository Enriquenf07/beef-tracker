package com.beeftracker.backend.veiculos.controllers;

import com.beeftracker.backend.base.exceptions.InvalidFormException;
import com.beeftracker.backend.base.exceptions.ResourceNotFoundException;
import com.beeftracker.backend.veiculos.models.VeiculoData;
import com.beeftracker.backend.veiculos.services.VeiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/veiculos")
public class VeiculoController {

    @Autowired
    private VeiculoService veiculoService;

    @GetMapping
    public ResponseEntity<?> listar(
            @RequestParam(required = false) String chave,
            @RequestParam(required = false) Boolean status) {
        return ResponseEntity.ok(veiculoService.pesquisar(chave, status));
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody VeiculoData data) throws InvalidFormException {
        veiculoService.salvar(data);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody VeiculoData data)
            throws InvalidFormException, ResourceNotFoundException {
        veiculoService.editar(id, data);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> alterarStatus(@PathVariable Long id) throws ResourceNotFoundException {
        veiculoService.alterarStatus(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) throws ResourceNotFoundException {
        veiculoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}