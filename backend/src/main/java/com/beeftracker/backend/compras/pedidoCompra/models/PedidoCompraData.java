package com.beeftracker.backend.compras.pedidoCompra.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.beeftracker.backend.base.exceptions.InvalidFormException;
import io.micrometer.common.util.StringUtils;

public record PedidoCompraData(
        String nome,
        String fornecedor_id,
        String fornecedor_nome,
        BigDecimal valor_total,
        String observacao,
        LocalDateTime dataEmissao,
        LocalDateTime dataEntrega,
        String status,
        Boolean ativo,
        String endereco) {
    public void validate() throws InvalidFormException {
        if (StringUtils.isBlank(nome)) {
            throw new InvalidFormException();
        }
    }
}
