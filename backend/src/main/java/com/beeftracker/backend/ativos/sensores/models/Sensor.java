package com.beeftracker.backend.ativos.sensores.models;

import org.springframework.data.relational.core.mapping.Embedded;

import com.beeftracker.backend.auth.models.metadata.Metadata;

public record Sensor(
        @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL) SensorData data,
        @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL) Metadata metadata) {
}
