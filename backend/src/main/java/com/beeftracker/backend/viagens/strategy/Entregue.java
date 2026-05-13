package com.beeftracker.backend.viagens.strategy;

import org.springframework.stereotype.Service;

import com.beeftracker.backend.viagens.model.StatusViagem;
import com.beeftracker.backend.viagens.model.Viagem;

@Service
public class Entregue extends AlterarStatus {

    @Override
    boolean validarStatus(StatusViagem atual) {
        return atual == StatusViagem.EM_TRANSITO || atual == StatusViagem.PENDENTE;
    }

    @Override
    void sideEffect(Viagem Viagem) {

    }

        @Override
    StatusViagem getStatus() {
       return StatusViagem.ENTREGUE   ;
    }

}
