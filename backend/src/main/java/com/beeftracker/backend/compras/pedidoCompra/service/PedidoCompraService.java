package com.beeftracker.backend.compras.pedidoCompra.service;

import com.beeftracker.backend.base.exceptions.ResourceNotFoundException;
import com.beeftracker.backend.compras.pedidoCompra.models.LoteBruto;
import com.beeftracker.backend.compras.pedidoCompra.models.LoteBrutoData;
import com.beeftracker.backend.compras.pedidoCompra.models.PedidoCompra;
import com.beeftracker.backend.compras.pedidoCompra.models.PedidoCompraData;
import com.beeftracker.backend.compras.pedidoCompra.repositories.PedidoCompraRepository;
import io.micrometer.common.util.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class PedidoCompraService {

    private final PedidoCompraRepository repository;

    public PedidoCompraService(PedidoCompraRepository repository) {
        this.repository = repository;
    }

    public void criar(PedidoCompraData data) {
        validarPedido(data);
        repository.salvar(data);
    }

    public void editar(Long id, PedidoCompraData data) throws ResourceNotFoundException {
        carregarOuLancarErro(id);
        validarPedido(data);
        repository.editar(id, data);
    }

    public void atualizarStatus(Long id, String novoStatus) throws ResourceNotFoundException {
        PedidoCompra pedido = carregarOuLancarErro(id);
        validarTransicaoStatus(pedido.data().status(), novoStatus);
        repository.editarStatus(id, novoStatus);
    }

    public PedidoCompra carregar(Long id) throws ResourceNotFoundException {
        return carregarOuLancarErro(id);
    }

    public List<PedidoCompra> pesquisar(Long fornecedorId, String status, int page) {
        return repository.pesquisar(fornecedorId, status, page);
    }


    public void criarLote(LoteBrutoData data) throws ResourceNotFoundException {
        carregarOuLancarErro(data.pedidoCompraId());
        validarLote(data);
        repository.salvarLote(data);
    }

    public void editarLote(Long id, LoteBrutoData data) throws ResourceNotFoundException {
        carregarLoteOuLancarErro(id);
        validarLote(data);
        repository.editarLote(id, data);
    }

    public LoteBruto carregarLote(Long id) throws ResourceNotFoundException {
        return carregarLoteOuLancarErro(id);
    }

    public List<LoteBruto> pesquisarLotes(Long pedidoCompraId) throws ResourceNotFoundException {
        carregarOuLancarErro(pedidoCompraId);
        return repository.pesquisarLotes(pedidoCompraId);
    }

    private PedidoCompra carregarOuLancarErro(Long id) throws ResourceNotFoundException {
        try {
            return repository.carregar(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException();
        }
    }

    private LoteBruto carregarLoteOuLancarErro(Long id) throws ResourceNotFoundException {
        try {
            return repository.carregarLote(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException();
        }
    }

    private void validarPedido(PedidoCompraData data) {
        if (data.fornecedorId() == null) {
            throw new IllegalArgumentException("Fornecedor é obrigatório.");
        }
        if (data.valorTotal() == null || data.valorTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor total deve ser maior que zero.");
        }
        if (data.dataEntrega() != null && data.dataEntrega().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Data de entrega não pode ser no passado.");
        }
    }

    private void validarLote(LoteBrutoData data) {
        if (StringUtils.isBlank(data.nome())) {
            throw new IllegalArgumentException("Nome do lote é obrigatório.");
        }
        if (data.peso() == null || data.peso() <= 0) {
            throw new IllegalArgumentException("Peso do lote deve ser maior que zero.");
        }
    }

    private void validarTransicaoStatus(String statusAtual, String novoStatus) {
        Map<String, List<String>> transicoesPermitidas = Map.of(
                "RASCUNHO",   List.of("ENVIADO", "CANCELADO"),
                "ENVIADO",    List.of("CONFIRMADO", "CANCELADO"),
                "CONFIRMADO", List.of("FINALIZADO", "CANCELADO"),
                "FINALIZADO", List.of(),
                "CANCELADO",  List.of()
        );

        List<String> permitidos = transicoesPermitidas.getOrDefault(statusAtual, List.of());
        if (!permitidos.contains(novoStatus)) {
            throw new IllegalStateException(
                    String.format("Transição de status inválida: %s → %s", statusAtual, novoStatus)
            );
        }
    }
}
