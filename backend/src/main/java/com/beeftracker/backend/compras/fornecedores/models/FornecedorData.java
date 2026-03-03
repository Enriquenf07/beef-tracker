package com.beeftracker.backend.compras.fornecedores.models;

public record FornecedorData(
        String nome,
        String apelido,
        String cnpj,
        String cep,
        Boolean ativo,
        String endereco) {

}
