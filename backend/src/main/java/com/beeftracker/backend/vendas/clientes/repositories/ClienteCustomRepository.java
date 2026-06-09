package com.beeftracker.backend.vendas.clientes.repositories;

import com.beeftracker.backend.vendas.clientes.models.Cliente;
import com.beeftracker.backend.vendas.clientes.models.ClienteData;

import java.util.List;

public interface ClienteCustomRepository {
    Long salvar(ClienteData cliente);

    void atualizar(Long id, ClienteData clienteData);

    void atualizarStatus(Long id, Boolean status);

    List<Cliente> pesquisar(String chave, Boolean status);
}