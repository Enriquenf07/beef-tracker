package com.beeftracker.backend.pedidos.repositories;

public interface PedidoVendaRepository {

    List<PedidoVenda> pesquisar(String chave, String status);

    Long salvar(PedidoVendaData data);

    void atualizar(Long id, PedidoVendaData data);

    void atualizarStatus(Long id, String status);

    Optional<PedidoVenda> buscarPorId(Long id);
}