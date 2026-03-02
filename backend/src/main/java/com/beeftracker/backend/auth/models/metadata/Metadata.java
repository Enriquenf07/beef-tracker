package com.beeftracker.backend.auth.models.metadata;

import java.time.LocalDate;

public record Metadata(
        LocalDate criado_em,
        LocalDate atualizado_em,
        Long id,
        String token) {
}