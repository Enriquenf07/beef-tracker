package com.beeftracker.backend.compras.pedidoCompra.models;

import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

import com.beeftracker.backend.auth.models.metadata.Metadata;

@Table("pedido_compra")
public record PedidoCompra(
        @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL) PedidoCompraData data,
        @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL) Metadata metadata) {
}

