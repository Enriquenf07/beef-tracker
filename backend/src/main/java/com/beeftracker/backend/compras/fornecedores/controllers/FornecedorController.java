package com.beeftracker.backend.compras.fornecedores.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.beeftracker.backend.compras.fornecedores.models.FornecedorData;
import com.beeftracker.backend.compras.fornecedores.services.FornecedorService;

@RestController
@RequestMapping("/compras/fornecedor")
public class FornecedorController {
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
    public ResponseEntity<?> cadastrar(@RequestBody FornecedorData fornecedorData) {
        fornecedorService.cadastrar(fornecedorData);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody FornecedorData fornecedorData) {
        fornecedorService.atualizar(id, fornecedorData);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(@PathVariable Long id, @RequestBody StatusData status) {
        fornecedorService.atualizarStatus(id, status.status());
        return ResponseEntity.ok().build();
    }
}

record StatusData(Boolean status) {

}
