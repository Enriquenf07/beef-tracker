package com.beeftracker.backend.compras.fornecedores.controllers;

import com.beeftracker.backend.base.BaseController;
import com.beeftracker.backend.base.exceptions.InvalidFormException;
import com.beeftracker.backend.base.exceptions.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.beeftracker.backend.compras.fornecedores.models.FornecedorData;
import com.beeftracker.backend.compras.fornecedores.services.FornecedorService;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/compras/fornecedor")
public class FornecedorController extends BaseController {
    public FornecedorService fornecedorService;

    public FornecedorController(
            FornecedorService fornecedorService) {
        this.fornecedorService = fornecedorService;
    }

    @GetMapping
    public ResponseEntity<?> pesquisar(@RequestParam(required = false) String chave, @RequestParam(required = false) Boolean status) {
        return ResponseEntity.ok(fornecedorService.pesquisar(chave, status));
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody FornecedorData fornecedorData) throws Exception {
        Long id = fornecedorService.cadastrar(fornecedorData);
        return ResponseEntity.created(new URI("/compras/fornecedor/" + id)).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) throws ResourceNotFoundException {
        return ResponseEntity.ok(fornecedorService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody FornecedorData fornecedorData) throws ResourceNotFoundException, InvalidFormException {
        fornecedorService.atualizar(id, fornecedorData);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(@PathVariable Long id) throws ResourceNotFoundException {
        fornecedorService.atualizarStatus(id);
        return ResponseEntity.ok().build();
    }
}

record StatusData(Boolean status) {

}
