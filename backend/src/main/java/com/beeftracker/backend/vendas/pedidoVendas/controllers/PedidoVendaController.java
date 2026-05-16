package com.beeftracker.backend.vendas.pedidoVendas.controllers;

import com.beeftracker.backend.base.BaseController;
import com.beeftracker.backend.base.exceptions.ResourceNotFoundException;
import com.beeftracker.backend.vendas.pedidoVendas.form.AtualizarStatusForm;
import com.beeftracker.backend.vendas.pedidoVendas.models.LoteFracionado;
import com.beeftracker.backend.vendas.pedidoVendas.models.LoteFracionadoData;
import com.beeftracker.backend.vendas.pedidoVendas.models.PedidoVenda;
import com.beeftracker.backend.vendas.pedidoVendas.models.PedidoVendaData;
import com.beeftracker.backend.vendas.pedidoVendas.service.PedidoVendaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vendas/pedido")
public class PedidoVendaController extends BaseController {

    private final PedidoVendaService service;

    public PedidoVendaController(PedidoVendaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody PedidoVendaData data) {
        service.criar(data);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody PedidoVendaData data)
            throws ResourceNotFoundException {
        service.editar(id, data);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(@PathVariable Long id, @RequestBody AtualizarStatusForm request)
            throws ResourceNotFoundException {
        service.atualizarStatus(id, request.status());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoVenda> carregar(@PathVariable Long id) throws ResourceNotFoundException {
        return ResponseEntity.ok(service.carregar(id));
    }

    @GetMapping
    public ResponseEntity<List<PedidoVenda>> pesquisar(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(service.pesquisar(clienteId, status, page));
    }

    @PostMapping("/{pedidoId}/lote")
    public ResponseEntity<?> criarLote(@PathVariable Long pedidoId, @RequestBody LoteFracionadoData data)
            throws ResourceNotFoundException {
        service.criarLote(new LoteFracionadoData(data.nome(), data.descricao(), data.peso(), pedidoId));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{pedidoId}/lote/{id}")
    public ResponseEntity<?> editarLote(@PathVariable Long pedidoId, @PathVariable Long id,
            @RequestBody LoteFracionadoData data) throws ResourceNotFoundException {
        service.editarLote(id, new LoteFracionadoData(data.nome(), data.descricao(), data.peso(), pedidoId));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{pedidoId}/lote/{id}")
    public ResponseEntity<LoteFracionado> carregarLote(@PathVariable Long pedidoId, @PathVariable Long id)
            throws ResourceNotFoundException {
        return ResponseEntity.ok(service.carregarLote(id));
    }

    @GetMapping("/{pedidoId}/lote")
    public ResponseEntity<List<LoteFracionado>> pesquisarLotes(@PathVariable Long pedidoId)
            throws ResourceNotFoundException {
        return ResponseEntity.ok(service.pesquisarLotes(pedidoId));
    }
}