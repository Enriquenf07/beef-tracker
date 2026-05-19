package com.beeftracker.backend.viagens.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.beeftracker.backend.base.exceptions.InvalidFormException;

import io.micrometer.common.util.StringUtils;
import lombok.With;

@With
public record ViagemData(
        Long veiculoId,
        Long sensorId,
        String sensorToken,
        String descricao,
        StatusViagem statusViagem,
        LocalDateTime saidaEm,
        LocalDateTime saidaRealEm,
        LocalDateTime entregueEm,
        LocalDateTime atualizadoEm) {

    public void validate() throws InvalidFormException {
        if (StringUtils.isBlank(descricao)) {
            throw new InvalidFormException();
        }
    }

}
