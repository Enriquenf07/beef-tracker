package com.beeftracker.backend.viagens.service;

import java.util.HashMap;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import com.beeftracker.backend.base.exceptions.ResourceNotFoundException;
import com.beeftracker.backend.compras.pedidoCompra.models.PedidoCompraData;
import com.beeftracker.backend.viagens.model.NovoStatus;
import com.beeftracker.backend.viagens.model.StatusViagem;
import com.beeftracker.backend.viagens.model.Viagem;
import com.beeftracker.backend.viagens.model.ViagemData;
import com.beeftracker.backend.viagens.repository.ViagemRepository;
import com.beeftracker.backend.viagens.strategy.AlterarStatus;
import com.beeftracker.backend.viagens.strategy.Cancelada;
import com.beeftracker.backend.viagens.strategy.EmTransito;
import com.beeftracker.backend.viagens.strategy.Entregue;
@Service
public class ViagemService {
    private final ViagemRepository repository;
    private final HashMap<String, AlterarStatus> services;

    public ViagemService(
            ViagemRepository repository,
            EmTransito transito,
            Entregue concluida,
            Cancelada cancelada) {
        this.repository = repository;
        services = new HashMap<>();
        services.put("EM_TRANSITO", transito);
        services.put("CONCLUIDA", concluida);
        services.put("CANCELADA", cancelada);
    }

    public void editar(Long id, String descricao) throws ResourceNotFoundException {
        Viagem viagem = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException());
        viagem = new Viagem(new ViagemData(
                viagem.data().veiculoId(),
                viagem.data().sensorId(),
                descricao,
                viagem.data().statusViagem(),
                viagem.data().saidaEm(),
                viagem.data().entregueEm(),
                viagem.data().atualizadoEm()), viagem.metadata());
        repository.editar(viagem.data(), id);
    }

    public void alterarStatus(Long id, NovoStatus status) throws ResourceNotFoundException {
        Viagem viagem = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException());
        AlterarStatus service = services.get(status.toString());
        viagem = service.alterarStatus(viagem, StatusViagem.valueOf(status.novoStatus()));
        repository.editar(viagem.data(), id);
    }

    public List<Viagem> pesquisar(String status, int page) {
        return repository.findByStatus(status, page);
    }

    public void criar(ViagemData data) {
        ViagemData novaViagem = new ViagemData(
                data.veiculoId(),
                data.sensorId(),
                data.descricao(),
                StatusViagem.PENDENTE,
                data.saidaEm(),
                data.entregueEm(),
                data.atualizadoEm());

        repository.criar(novaViagem);
    }

}