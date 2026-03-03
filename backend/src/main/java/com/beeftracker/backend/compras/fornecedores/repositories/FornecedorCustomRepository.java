package com.beeftracker.backend.compras.fornecedores.repositories;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

import com.beeftracker.backend.compras.fornecedores.models.FornecedorData;

public interface FornecedorCustomRepository {
    Long salvar(FornecedorData fornecedor);

    void atualizar(Long id, FornecedorData fornecedorData);
    void atualizarStatus(Long id, Boolean status);
}
