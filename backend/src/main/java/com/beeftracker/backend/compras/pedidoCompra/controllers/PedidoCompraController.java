package com.beeftracker.backend.compras.pedidoCompra.controllers;

import com.beeftracker.backend.base.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("/compras/pedido")
public class PedidoCompraController extends BaseController {
    


    @GetMapping
    public ResponseEntity<?> pesquisar(@RequestParam(required = false) String chave, @RequestParam(required = false) Boolean status) {
        return ResponseEntity.ok().build();
    }


}
