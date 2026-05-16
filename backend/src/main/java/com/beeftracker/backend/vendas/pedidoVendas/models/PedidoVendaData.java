package com.beeftracker.backend.vendas.pedidoVendas.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PedidoVendaData(
        Long clienteId,
        BigDecimal valorTotal,
        String status,
        String observacao,
        LocalDateTime dataVenda,
        LocalDate dataVencimento) {
}