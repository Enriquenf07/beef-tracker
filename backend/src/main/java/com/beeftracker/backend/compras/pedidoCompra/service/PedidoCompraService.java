package com.beeftracker.backend.compras.pedidoCompra.service;

import com.beeftracker.backend.base.exceptions.InvalidFormException;
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

    public void criar(PedidoCompraData data) throws InvalidFormException {
        validarPedido(data);
        repository.salvar(data);
    }

    public void editar(Long id, PedidoCompraData data) throws ResourceNotFoundException, InvalidFormException {
        carregarOuLancarErro(id);
        validarPedido(data);
        repository.editar(id, data);
    }

    public void atualizarStatus(Long id, String novoStatus) throws ResourceNotFoundException, InvalidFormException {
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


    public void criarLote(LoteBrutoData data) throws ResourceNotFoundException, InvalidFormException {
        carregarOuLancarErro(data.pedidoCompraId());
        validarLote(data);
        repository.salvarLote(data);
    }

    public void editarLote(Long id, LoteBrutoData data) throws ResourceNotFoundException, InvalidFormException {
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

    public LoteBruto carregarLoteOuLancarErro(Long id) throws ResourceNotFoundException {
        try {
            return repository.carregarLote(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException();
        }
    }

    private void validarPedido(PedidoCompraData data) throws InvalidFormException {
        if (data.fornecedorId() == null) {
            throw new InvalidFormException();
        }
        if (data.valorTotal() == null || data.valorTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidFormException();
        }
    }


    public void vincularViagem(Long id, Long viagemId) throws ResourceNotFoundException {
        PedidoCompra pedido = carregarOuLancarErro(id);

        if (pedido.data().status() == "CANCELADO") {
            throw new IllegalStateException("Não é possível vincular viagem a um pedido cancelado.");
        }
        repository.vincularViagem(id, viagemId);
    }


    private void validarLote(LoteBrutoData data) throws InvalidFormException {
        if (StringUtils.isBlank(data.nome())) {
            throw new InvalidFormException();

        }
        if (data.peso() == null || data.peso() <= 0) {
            throw new InvalidFormException();

        }
    }

    private void validarTransicaoStatus(String statusAtual, String novoStatus) throws InvalidFormException {
        Map<String, List<String>> transicoesPermitidas = Map.of(
                "PENDENTE", List.of("ENTREGUE", "CANCELADO"),
                "ENTREGUE", List.of(),
                "CANCELADO", List.of());


        List<String> permitidos = transicoesPermitidas.getOrDefault(statusAtual, List.of());
        if (!permitidos.contains(novoStatus)) {
            throw new InvalidFormException();
        }
    }
}
