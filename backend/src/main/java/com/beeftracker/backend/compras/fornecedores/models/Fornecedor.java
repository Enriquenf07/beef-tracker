package com.beeftracker.backend.compras.fornecedores.models;

import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

import com.beeftracker.backend.auth.models.metadata.Metadata;


@Table("fornecedores")
public record Fornecedor(
        @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL) FornecedorData data,
        @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL) Metadata metadata,
        Boolean ativo) {
}
