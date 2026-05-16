package com.beeftracker.backend.vendas.clientes.models;

import com.beeftracker.backend.base.exceptions.InvalidFormException;
import io.micrometer.common.util.StringUtils;

public record ClienteData(
        String nome,
        String apelido,
        String cpfCnpj,
        String email,
        String telefone,
        String cep,
        String uf,
        Boolean ativo) {

    public void validate() throws InvalidFormException {
        if (StringUtils.isBlank(nome)) {
            throw new InvalidFormException();
        }
    }
}