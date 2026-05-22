package com.beeftracker.backend.viagens.controller;

import java.time.LocalDate;
import java.util.List;

import com.beeftracker.backend.base.Page;
import com.beeftracker.backend.base.exceptions.SensorIndisponivelException;
import com.beeftracker.backend.viagens.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import com.beeftracker.backend.base.exceptions.ResourceNotFoundException;
import com.beeftracker.backend.compras.pedidoCompra.models.PedidoCompra;
import com.beeftracker.backend.compras.pedidoCompra.models.PedidoCompraData;
import com.beeftracker.backend.viagens.service.ViagemService;

@RestController
@RequestMapping("/viagem")
public class ViagemController {
    private final ViagemService service;

    @Autowired
    public ViagemController(ViagemService service){
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody ViagemData data) throws ResourceNotFoundException {
        service.criar(data);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<Page<Viagem>> pesquisar(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim,
            @RequestParam(defaultValue = "0") Integer page) {
        return ResponseEntity.ok(service.pesquisar(status, dataInicio, dataFim, page));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> alterarStatus(@PathVariable Long id, @RequestBody NovoStatus status) throws ResourceNotFoundException, SensorIndisponivelException {
        service.alterarStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody ViagemData data) throws ResourceNotFoundException {
        service.editar(id, data.descricao());
        return ResponseEntity.ok().build();

    }

    @GetMapping("/{id}/leituras")
    public ResponseEntity<?> getLeituras(@PathVariable Long id) throws ResourceNotFoundException {
        return ResponseEntity.ok(service.getLeituras(id));
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<?> getStats(@PathVariable Long id) throws ResourceNotFoundException {
        return ResponseEntity.ok(service.getStats(id));
    }

}
