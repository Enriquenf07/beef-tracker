package com.beeftracker.backend.pedidos.models;

import com.beeftracker.backend.base.exceptions.InvalidFormException;
import io.micrometer.common.util.StringUtils;

public record PedidoVendaData(
        String cliente,
        Double valorTotal
) {

    public void validate() throws InvalidFormException {
        if (StringUtils.isBlank(cliente)) {
            throw new InvalidFormException("Cliente obrigatório");
        }

        if (valorTotal == null || valorTotal <= 0) {
            throw new InvalidFormException("Valor inválido");
        }
    }
}