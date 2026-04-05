package com.beeftracker.backend.compras.pedidoCompra.repositories;

import java.util.List;

import com.beeftracker.backend.compras.pedidoCompra.models.LoteBruto;
import com.beeftracker.backend.compras.pedidoCompra.models.LoteBrutoData;
import com.beeftracker.backend.compras.pedidoCompra.models.PedidoCompra;
import com.beeftracker.backend.compras.pedidoCompra.models.PedidoCompraData;

public interface PedidoCompraRepository {
    void salvar(PedidoCompraData data);

    void editar(Long id, PedidoCompraData data);

    void editarStatus(Long id, String status);

    PedidoCompra carregar(Long id);

    List<PedidoCompra> pesquisar(Long fornecedorId, String status);

    void salvarLote(LoteBrutoData data);

    void editarLote(Long id, LoteBrutoData data);

    LoteBruto carregarLote(Long id);

    List<LoteBruto> pesquisarLotes(Long pedidoCompraId);
}
