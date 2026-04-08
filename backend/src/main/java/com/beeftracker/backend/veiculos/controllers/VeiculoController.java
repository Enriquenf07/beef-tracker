package com.beeftracker.backend.veiculos.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.beeftracker.backend.base.BaseController;
import com.beeftracker.backend.base.exceptions.InvalidFormException;
import com.beeftracker.backend.veiculos.models.Veiculo;
import com.beeftracker.backend.veiculos.models.VeiculoData;
import com.beeftracker.backend.veiculos.services.VeiculoService;

import java.util.List;

@RestController
@RequestMapping("/api/veiculos")
public class VeiculoController extends BaseController {

    @Autowired
    private VeiculoService veiculoService;

    @PostMapping
    public ResponseEntity<Long> cadastrar(@RequestBody VeiculoData data) throws InvalidFormException {
        return ResponseEntity.ok(veiculoService.cadastrar(data));
    }

    @GetMapping
    public ResponseEntity<List<Veiculo>> listar(
            @RequestParam(required = false) String chave,
            @RequestParam(required = false) Boolean status) {
        // Chamando o método 'listar' que criamos no Service
        return ResponseEntity.ok(veiculoService.listar(chave, status));
    }
}