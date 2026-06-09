package com.beeftracker.backend.viagens.repository;

import com.beeftracker.backend.base.Page;
import com.beeftracker.backend.base.exceptions.ResourceNotFoundException;
import com.beeftracker.backend.viagens.model.Viagem;
import com.beeftracker.backend.viagens.model.ViagemData;

import java.time.LocalDate;
import java.util.List;

public interface ViagemCustomRepository {
    void criar(ViagemData viagem);

    void editar(ViagemData viagem, Long id);
    Viagem carregar(Long id) throws ResourceNotFoundException;
    Viagem carregar(String id) throws ResourceNotFoundException;
    Page<Viagem> findByStatusAndData(String status, LocalDate dataInicio, LocalDate dataFim, int page, Boolean isMotorista, Long id);

    List<Viagem> listAllPendente();
}
