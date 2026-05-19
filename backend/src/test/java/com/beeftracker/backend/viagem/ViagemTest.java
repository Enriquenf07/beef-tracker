package com.beeftracker.backend.viagem;

import com.influxdb.v3.client.InfluxDBClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.beeftracker.backend.base.exceptions.ResourceNotFoundException;
import com.beeftracker.backend.viagens.model.*;
import com.beeftracker.backend.viagens.repository.ViagemRepository;
import com.beeftracker.backend.viagens.service.ViagemService;
import com.beeftracker.backend.viagens.strategy.Cancelada;
import com.beeftracker.backend.viagens.strategy.EmTransito;
import com.beeftracker.backend.viagens.strategy.Entregue;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ViagemTest {

    private ViagemRepository repository;
    private EmTransito transito;
    private Entregue concluida;
    private Cancelada cancelada;
    private ViagemService service;
    private InfluxDBClient influxDBClient;

    @BeforeEach
    void setUp() {
        repository = mock(ViagemRepository.class);
        transito   = mock(EmTransito.class);
        concluida  = mock(Entregue.class);
        cancelada  = mock(Cancelada.class);
        service    = new ViagemService(repository, transito, concluida, cancelada, influxDBClient);
    }

    // --- criar ---

    @Test
    void criar_deveSalvarViagemComStatusPendente() {
        ViagemData data = new ViagemData(1L, 2L, "", "desc", StatusViagem.CANCELADA, null, null, null, null);
        service.criar(data);

        verify(repository).criar(argThat(d -> d.statusViagem() == StatusViagem.PENDENTE));
    }

    // --- editar ---

    @Test
    void editar_deveAtualizarDescricao() throws ResourceNotFoundException {
        ViagemData data = new ViagemData(1L, 2L, "", "antiga", StatusViagem.PENDENTE, null, null, null, null);
        Viagem viagem   = new Viagem(data, null);
        when(repository.findById(1L)).thenReturn(Optional.of(viagem));

        service.editar(1L, "nova");

        verify(repository).editar(argThat(d -> "nova".equals(d.descricao())), eq(1L));
    }

    @Test
    void editar_quandoNaoEncontrado_deveLancarException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.editar(99L, "x"));
    }

    // --- alterarStatus ---

    @Test
    void alterarStatus_deveUsarServicoCorreto() throws ResourceNotFoundException {
        ViagemData data   = new ViagemData(1L, 2L,"", "desc", StatusViagem.PENDENTE, null, null, null, null);
        Viagem viagem     = new Viagem(data, null);
        Viagem atualizada = new Viagem(
                new ViagemData(1L, 2L, "", "desc", StatusViagem.EM_TRANSITO, null, null, null,null), null);

        when(repository.findById(1L)).thenReturn(Optional.of(viagem));
        when(transito.alterarStatus(viagem, StatusViagem.EM_TRANSITO)).thenReturn(atualizada);

        NovoStatus status = mock(NovoStatus.class);
        when(status.toString()).thenReturn("EM_TRANSITO");
        when(status.novoStatus()).thenReturn("EM_TRANSITO");

        service.alterarStatus(1L, status);

        verify(transito).alterarStatus(viagem, StatusViagem.EM_TRANSITO);
        verify(repository).editar(atualizada.data(), 1L);
    }

    // --- pesquisar ---

    @Test
    void pesquisar_deveRepassarParametrosAoRepository() {
        service.pesquisar("PENDENTE", 0);

        verify(repository).findByStatus("PENDENTE", 0);
    }
}