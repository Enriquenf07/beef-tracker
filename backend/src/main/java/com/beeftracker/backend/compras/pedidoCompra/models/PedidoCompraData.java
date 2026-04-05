package com.beeftracker.backend.compras.pedidoCompra.models;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PedidoCompraData(
        Long fornecedorId,
        BigDecimal valorTotal,
        String status,
        String observacao,
        LocalDate dataEmissao,
        LocalDate dataEntrega
) {}