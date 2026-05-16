package com.beeftracker.backend.vendas.pedidoVendas.repositories;

import java.util.List;

import com.beeftracker.backend.vendas.pedidoVendas.models.LoteFracionado;
import com.beeftracker.backend.vendas.pedidoVendas.models.LoteFracionadoData;
import com.beeftracker.backend.vendas.pedidoVendas.models.PedidoVenda;
import com.beeftracker.backend.vendas.pedidoVendas.models.PedidoVendaData;

public interface PedidoVendaRepository {

    void salvar(PedidoVendaData data);

    void editar(Long id, PedidoVendaData data);

    void editarStatus(Long id, String status);

    PedidoVenda carregar(Long id);

    List<PedidoVenda> pesquisar(Long clienteId, String status, int page);

    void salvarLote(LoteFracionadoData data);

    void editarLote(Long id, LoteFracionadoData data);

    LoteFracionado carregarLote(Long id);

    List<LoteFracionado> pesquisarLotes(Long pedidoVendaId);
}