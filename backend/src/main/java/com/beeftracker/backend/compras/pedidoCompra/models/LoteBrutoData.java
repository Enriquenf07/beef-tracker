package com.beeftracker.backend.compras.pedidoCompra.models;

public record LoteBrutoData(
        String nome,
        String descricao,
        Integer peso,
        Long pedidoCompraId
) {}