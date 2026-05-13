package com.beeftracker.backend.viagens.strategy;

import org.springframework.stereotype.Service;

import com.beeftracker.backend.viagens.model.StatusViagem;
import com.beeftracker.backend.viagens.model.Viagem;
@Service
public class EmTransito extends AlterarStatus{

    @Override
    boolean validarStatus(StatusViagem atual) {
        return atual == StatusViagem.PENDENTE;
    }

    @Override
    void sideEffect(Viagem Viagem) {
    }

        @Override
    StatusViagem getStatus() {
       return StatusViagem.EM_TRANSITO   ;
    }
    
}
