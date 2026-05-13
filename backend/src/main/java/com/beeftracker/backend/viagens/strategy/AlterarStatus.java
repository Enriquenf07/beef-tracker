package com.beeftracker.backend.viagens.strategy;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.beeftracker.backend.viagens.model.StatusViagem;
import com.beeftracker.backend.viagens.model.Viagem;
import com.beeftracker.backend.viagens.model.ViagemData;
@Service
public abstract class AlterarStatus {
    abstract boolean validarStatus(StatusViagem atual);
    abstract StatusViagem getStatus();
    public Viagem atualizarData(Viagem viagem){
        return new Viagem(new ViagemData(
            viagem.data().veiculoId(),
            viagem.data().sensorId(),
            viagem.data().descricao(),
            getStatus(),
            viagem.data().saidaEm(),
            viagem.data().entregueEm(),
            LocalDateTime.now()
        ), viagem.metadata());
    }

    public Viagem alterarStatus(Viagem viagem, StatusViagem atual){
        validarStatus(atual);
        sideEffect(viagem);
        return atualizarData(viagem);
    }
    abstract void sideEffect(Viagem viagem);
}
