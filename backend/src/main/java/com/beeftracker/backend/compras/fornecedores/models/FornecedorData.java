package com.beeftracker.backend.compras.fornecedores.models;

import com.beeftracker.backend.base.exceptions.InvalidFormException;
import io.micrometer.common.util.StringUtils;

public record FornecedorData(
        String nome,
        String apelido,
        String cnpj,
        String cep,
        Boolean ativo,
        String endereco) {
    public void validate() throws InvalidFormException {
        if(StringUtils.isBlank(nome)){
            throw new InvalidFormException();
        }
    }
}
