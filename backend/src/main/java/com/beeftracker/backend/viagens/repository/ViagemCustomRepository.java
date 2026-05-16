package com.beeftracker.backend.viagens.repository;

import com.beeftracker.backend.viagens.model.Viagem;
import com.beeftracker.backend.viagens.model.ViagemData;

public interface ViagemCustomRepository {
    void criar(ViagemData viagem);

    void editar(ViagemData viagem, Long id);
}
