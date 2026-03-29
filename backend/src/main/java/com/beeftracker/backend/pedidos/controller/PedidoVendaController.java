package com.beeftracker.backend.pedidos.controller;

import com.beeftracker.backend.base.BaseController;
import com.beeftracker.backend.base.exceptions.InvalidFormException;
import com.beeftracker.backend.base.exceptions.ResourceNotFoundException;
import com.beeftracker.backend.vendas.pedidos.models.PedidoVendaData;
import com.beeftracker.backend.vendas.pedidos.services.PedidoVendaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/vendas/pedidos")
public class PedidoVendaController extends BaseController {

    public PedidoVendaService pedidoVendaService;

    public PedidoVendaController(PedidoVendaService pedidoVendaService) {
        this.pedidoVendaService = pedidoVendaService;
    }

    @GetMapping
    public ResponseEntity<?> pesquisar(
            @RequestParam(required = false) String chave,
            @RequestParam(required = false) Boolean status) {

        return ResponseEntity.ok(pedidoVendaService.pesquisar(chave, status));
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody PedidoVendaData data) throws Exception {

        Long id = pedidoVendaService.cadastrar(data);

        return ResponseEntity.created(new URI("/vendas/pedidos/" + id)).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) throws ResourceNotFoundException {

        return ResponseEntity.ok(pedidoVendaService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable Long id,
            @RequestBody PedidoVendaData data)
            throws ResourceNotFoundException, InvalidFormException {

        pedidoVendaService.atualizar(id, data);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(@PathVariable Long id)
            throws ResourceNotFoundException {

        pedidoVendaService.atualizarStatus(id);

        return ResponseEntity.ok().build();
    }
}