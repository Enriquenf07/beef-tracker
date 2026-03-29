package com.beeftracker.backend.pedidos.models;

import com.beeftracker.backend.auth.models.metadata.Metadata;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

@Table("pedido_venda")
public record PedidoVenda(

        @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL)
        PedidoVendaData data,

        @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL)
        Metadata metadata

) {}