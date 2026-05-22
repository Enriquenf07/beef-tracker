package com.beeftracker.backend.viagens.strategy;

import com.beeftracker.backend.base.exceptions.SensorIndisponivelException;
import com.beeftracker.backend.viagens.model.ViagemData;
import com.beeftracker.backend.viagens.repository.ViagemRepository;
import org.springframework.stereotype.Service;

import com.beeftracker.backend.viagens.model.StatusViagem;
import com.beeftracker.backend.viagens.model.Viagem;

import java.time.LocalDateTime;

@Service
public class EmTransito extends AlterarStatus {

    private final ViagemRepository repository;

    public EmTransito(ViagemRepository repository) {
        this.repository = repository;
    }

    @Override
    boolean validarStatus(StatusViagem atual) {
        return atual == StatusViagem.PENDENTE;
    }

    @Override
    Viagem sideEffect(Viagem viagem) throws SensorIndisponivelException {
        Viagem ativa = repository.findBySensorIdAtiva(viagem.data().sensorId());
        if (ativa != null) {
            throw new SensorIndisponivelException();
        }

        ViagemData dadosAtualizados = viagem.data()
                .withSaidaRealEm(LocalDateTime.now());
        return new Viagem(dadosAtualizados, viagem.metadata());
    }

    @Override
    StatusViagem getStatus() {
        return StatusViagem.EM_TRANSITO;
    }
}