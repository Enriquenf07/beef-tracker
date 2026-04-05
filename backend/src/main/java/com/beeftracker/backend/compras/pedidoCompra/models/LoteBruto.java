package com.beeftracker.backend.compras.pedidoCompra.models;

import com.beeftracker.backend.auth.models.metadata.Metadata;

public record LoteBruto(
        LoteBrutoData data,
        Metadata metadata
) {}
