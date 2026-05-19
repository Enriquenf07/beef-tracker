package com.beeftracker.backend.viagens.strategy;

import com.beeftracker.backend.viagens.model.ViagemData;
import org.springframework.stereotype.Service;

import com.beeftracker.backend.viagens.model.StatusViagem;
import com.beeftracker.backend.viagens.model.Viagem;

import java.time.LocalDateTime;

@Service
public class EmTransito extends AlterarStatus{

    @Override
    boolean validarStatus(StatusViagem atual) {
        return atual == StatusViagem.PENDENTE;
    }

    @Override
    Viagem sideEffect(Viagem viagem) {
        ViagemData dadosAtualizados = viagem.data()
                .withSaidaRealEm(LocalDateTime.now());
        return new Viagem(
                dadosAtualizados,
                viagem.metadata()
        );
    }

        @Override
    StatusViagem getStatus() {
       return StatusViagem.EM_TRANSITO   ;
    }
    
}
