package com.beeftracker.backend.vendas.clientes.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.beeftracker.backend.base.BaseController;
import com.beeftracker.backend.base.exceptions.InvalidFormException;
import com.beeftracker.backend.base.exceptions.ResourceNotFoundException;
import com.beeftracker.backend.vendas.clientes.models.ClienteData;
import com.beeftracker.backend.vendas.clientes.services.ClienteService;

@RestController
@RequestMapping("/vendas/cliente")
public class ClienteController extends BaseController {
    public ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public ResponseEntity<?> pesquisar(@RequestParam(required = false) String chave,
            @RequestParam(required = false) Boolean status) {
        return ResponseEntity.ok(clienteService.pesquisar(chave, status));
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody ClienteData clienteData) throws Exception {
        Long id = clienteService.cadastrar(clienteData);
        return ResponseEntity.created(new URI("/vendas/clientes/" + id)).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) throws ResourceNotFoundException {
        return ResponseEntity.ok(clienteService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody ClienteData clienteData)
            throws ResourceNotFoundException, InvalidFormException {
        clienteService.atualizar(id, clienteData);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(@PathVariable Long id) throws ResourceNotFoundException {
        clienteService.atualizarStatus(id);
        return ResponseEntity.ok().build();
    }

}
