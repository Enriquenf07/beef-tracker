package com.beeftracker.backend.vendas.pedidoVendas.models;

import com.beeftracker.backend.auth.models.metadata.Metadata;

public record LoteFracionado(
        LoteFracionadoData data,
        Metadata metadata) {
}