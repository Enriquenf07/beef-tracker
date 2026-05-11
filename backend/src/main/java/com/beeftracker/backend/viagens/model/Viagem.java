package com.beeftracker.backend.viagens.model;

import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

import com.beeftracker.backend.auth.models.metadata.Metadata;

@Table("viagem")
public record Viagem(
        @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL) ViagemData data,
        @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL) Metadata metadata) {
}
