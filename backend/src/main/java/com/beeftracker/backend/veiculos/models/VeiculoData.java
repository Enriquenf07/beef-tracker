package com.beeftracker.backend.veiculos.models;

import com.beeftracker.backend.base.exceptions.InvalidFormException;
import io.micrometer.common.util.StringUtils;
import java.math.BigDecimal;

public record VeiculoData(
        String placa,
        String modelo,
        String marca,
        Integer ano,
        BigDecimal capacidadeCarga,
        Boolean ativo) {

    public void validate() throws InvalidFormException {
        if (StringUtils.isBlank(placa) || StringUtils.isBlank(modelo) || capacidadeCarga == null) {
            throw new InvalidFormException();
        }
    }
}