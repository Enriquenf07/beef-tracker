package com.beeftracker.backend.veiculos.models;

import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;
import com.beeftracker.backend.auth.models.metadata.Metadata;
import org.springframework.data.annotation.Id;

@Table("veiculo")
public record Veiculo(
        @Id Long id,
        @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL) VeiculoData data,
        @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL) Metadata metadata) {
}