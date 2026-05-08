package com.beeftracker.backend.compras.pedidoCompra.controllers;

import com.beeftracker.backend.base.BaseController;
import com.beeftracker.backend.base.exceptions.ResourceNotFoundException;
import com.beeftracker.backend.compras.pedidoCompra.form.AtualizarStatusForm;
import com.beeftracker.backend.compras.pedidoCompra.models.LoteBruto;
import com.beeftracker.backend.compras.pedidoCompra.models.LoteBrutoData;
import com.beeftracker.backend.compras.pedidoCompra.models.PedidoCompra;
import com.beeftracker.backend.compras.pedidoCompra.models.PedidoCompraData;
import com.beeftracker.backend.compras.pedidoCompra.service.PedidoCompraService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/compras/pedido")
public class PedidoCompraController extends BaseController {

    private final PedidoCompraService service;

    public PedidoCompraController(PedidoCompraService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody PedidoCompraData data) {
        service.criar(data);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody PedidoCompraData data) throws ResourceNotFoundException {
        service.editar(id, data);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(@PathVariable Long id, @RequestBody AtualizarStatusForm request) throws ResourceNotFoundException {
        service.atualizarStatus(id, request.status());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoCompra> carregar(@PathVariable Long id) throws ResourceNotFoundException {
        return ResponseEntity.ok(service.carregar(id));
    }

    @GetMapping
    public ResponseEntity<List<PedidoCompra>> pesquisar(
            @RequestParam(required = false) Long fornecedorId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(service.pesquisar(fornecedorId, status, page));
    }
    @PostMapping("/{pedidoId}/lote")
    public ResponseEntity<?> criarLote(@PathVariable Long pedidoId, @RequestBody LoteBrutoData data) throws ResourceNotFoundException {
        service.criarLote(new LoteBrutoData(data.nome(), data.descricao(), data.peso(), pedidoId));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{pedidoId}/lote/{id}")
    public ResponseEntity<?> editarLote(@PathVariable Long pedidoId, @PathVariable Long id, @RequestBody LoteBrutoData data) throws ResourceNotFoundException {
        service.editarLote(id, new LoteBrutoData(data.nome(), data.descricao(), data.peso(), pedidoId));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{pedidoId}/lote/{id}")
    public ResponseEntity<LoteBruto> carregarLote(@PathVariable Long pedidoId, @PathVariable Long id) throws ResourceNotFoundException {
        return ResponseEntity.ok(service.carregarLote(id));
    }

    @GetMapping("/{pedidoId}/lote")
    public ResponseEntity<List<LoteBruto>> pesquisarLotes(@PathVariable Long pedidoId) throws ResourceNotFoundException {
        return ResponseEntity.ok(service.pesquisarLotes(pedidoId));
    }
}


