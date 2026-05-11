package com.beeftracker.backend.viagens.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.beeftracker.backend.base.exceptions.InvalidFormException;

import io.micrometer.common.util.StringUtils;

public record ViagemData(
        Integer veiculoId,
        Integer sensorId,
        String descricao,
        StatusViagem statusViagem,
        LocalDateTime saidaEm,
        LocalDateTime entregueEm,
        LocalDateTime atualizadoEm) {

    public void validate() throws InvalidFormException {
        if (StringUtils.isBlank(descricao)) {
            throw new InvalidFormException();
        }
    }

}
