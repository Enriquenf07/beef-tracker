package com.beeftracker.backend.vendas.pedidoVendas.models;

public record LoteFracionadoData(
        String nome,
        String descricao,
        Integer peso,
        Long pedidoVendaId) {
}