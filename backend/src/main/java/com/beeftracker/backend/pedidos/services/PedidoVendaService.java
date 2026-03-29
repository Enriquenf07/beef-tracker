package com.beeftracker.backend.pedidos.services;

import com.beeftracker.backend.base.exceptions.InvalidFormException;
import com.beeftracker.backend.base.exceptions.ResourceNotFoundException;
import com.beeftracker.backend.vendas.pedidos.models.PedidoVenda;
import com.beeftracker.backend.vendas.pedidos.models.PedidoVendaData;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class PedidoVendaService {

    private final PedidoVendaRepository repository;

    public PedidoVendaService(PedidoVendaRepository repository) {
        this.repository = repository;
    }

    public List<PedidoVenda> pesquisar(String chave, String status) {
        List<PedidoVenda> pedidos = (List<PedidoVenda>) repository.pesquisar(chave, status);
        return pedidos.stream()
                .sorted(Comparator.comparingLong(p -> p.metadata().id()))
                .toList();
    }

    public Long cadastrar(PedidoVendaData data) throws InvalidFormException {
        data.validate();

        data.setStatus("criado");

        return repository.salvar(data);
    }

    public void atualizar(Long id, PedidoVendaData data)
            throws ResourceNotFoundException, InvalidFormException {

        findById(id);
        data.validate();

        repository.atualizar(id, data);
    }

    public void atualizarStatus(Long id, String novoStatus)
            throws ResourceNotFoundException {

        PedidoVenda pedido = findById(id);


        if (pedido.data().status().equals("cancelado")) {
            return;
        }

        if (!novoStatus.equals("cancelado")) {
            throw new IllegalArgumentException("Status inválido para esta operação");
        }

        repository.atualizarStatus(id, novoStatus);
    }

    public PedidoVenda findById(Long id) throws ResourceNotFoundException {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException());
    }
}