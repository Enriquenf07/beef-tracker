package com.beeftracker.backend.compras.pedidoCompra.controllers;

import com.beeftracker.backend.base.BaseController;
import com.beeftracker.backend.compras.pedidoCompra.models.PedidoCompraData;
import com.beeftracker.backend.compras.pedidoCompra.repositories.PedidoCompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/compras/pedido")
@CrossOrigin(origins = "*")
public class PedidoCompraController extends BaseController {

    @Autowired
    private PedidoCompraRepository repository;

    @GetMapping
    public ResponseEntity<?> pesquisar(@RequestParam(required = false) Long fornecedorId,
            @RequestParam(required = false) String status) {

        return ResponseEntity.ok(repository.pesquisar(fornecedorId, status));
    }

    @PostMapping
    public ResponseEntity<?> salvar(@RequestBody PedidoCompraData data) {
        try {

            PedidoCompraData dadosParaSalvar = new PedidoCompraData(
                    data.fornecedorId(),
                    data.valorTotal(),
                    data.status(),
                    data.observacao(),
                    LocalDate.now(),
                    data.dataEntrega());

            repository.salvar(dadosParaSalvar);

            System.out.println("Pedido salvo com sucesso para o fornecedor: " + data.fornecedorId());

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Erro ao salvar no banco: " + e.getMessage());
        }
    }
}