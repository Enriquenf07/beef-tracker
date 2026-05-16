package com.beeftracker.backend.vendas.clientes.models;

import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

import com.beeftracker.backend.auth.models.metadata.Metadata;

@Table("clientes")
public record Cliente(
        @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL) ClienteData data,
        @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL) Metadata metadata) {
}
