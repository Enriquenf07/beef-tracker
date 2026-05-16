package com.beeftracker.backend.vendas.pedidoVendas.models;

import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

import com.beeftracker.backend.auth.models.metadata.Metadata;

@Table("pedido_venda")
public record PedidoVenda(
        @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL) PedidoVendaData data,
        @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL) Metadata metadata) {
}