package com.beeftracker.backend.vendas.clientes.repositories;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

import com.beeftracker.backend.vendas.clientes.models.ClienteData;

public interface ClienteCustomRepository {
    Long salvar(ClienteData cliente);

    void atualizar(Long id, ClienteData clienteData);

    void atualizarStatus(Long id, Boolean status);
}
