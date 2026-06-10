package com.beeftracker.backend.veiculo;

import com.beeftracker.backend.auth.models.metadata.Metadata;
import com.beeftracker.backend.base.exceptions.InvalidFormException;
import com.beeftracker.backend.base.exceptions.ResourceNotFoundException;
import com.beeftracker.backend.veiculos.models.Veiculo;
import com.beeftracker.backend.veiculos.models.VeiculoData;
import com.beeftracker.backend.veiculos.repositories.VeiculoRepository;
import com.beeftracker.backend.veiculos.services.VeiculoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VeiculoServiceTest {

    @Mock
    private VeiculoRepository veiculoRepository;

    @InjectMocks
    private VeiculoService service;

    private VeiculoData dataMock;
    private Veiculo veiculoMock;

    @BeforeEach
    void setUp() {
        dataMock = new VeiculoData("ABC1D23", "Scania R450", "Scania", 2022, new BigDecimal("20000"), true);
        Metadata metadata = new Metadata(LocalDate.now(), LocalDate.now(), 1L, "token-veiculo");
        veiculoMock = new Veiculo(dataMock, metadata);
    }

    @Test
    void salvar_devePersistirVeiculo_quandoDadosValidos() throws InvalidFormException {
        when(veiculoRepository.buscarPorPlaca("ABC1D23")).thenReturn(Optional.empty());
        when(veiculoRepository.salvar(any(VeiculoData.class))).thenReturn(1L);

        Long id = service.salvar(dataMock);

        verify(veiculoRepository).salvar(any(VeiculoData.class));
        assertThat(id).isEqualTo(1L);
    }

    @Test
    void salvar_deveLancarExcecao_quandoPlacaJaCadastrada() {
        when(veiculoRepository.buscarPorPlaca("ABC1D23")).thenReturn(Optional.of(veiculoMock));

        assertThatThrownBy(() -> service.salvar(dataMock))
                .isInstanceOf(InvalidFormException.class);

        verify(veiculoRepository, never()).salvar(any());
    }

    @Test
    void salvar_deveLancarExcecao_quandoPlacaEmBranco() {
        VeiculoData invalido = new VeiculoData("", "Scania R450", "Scania", 2022, new BigDecimal("20000"), true);

        assertThatThrownBy(() -> service.salvar(invalido))
                .isInstanceOf(InvalidFormException.class);

        verify(veiculoRepository, never()).salvar(any());
    }

    @Test
    void salvar_deveLancarExcecao_quandoModeloEmBranco() {
        VeiculoData invalido = new VeiculoData("ABC1D23", "", "Scania", 2022, new BigDecimal("20000"), true);

        assertThatThrownBy(() -> service.salvar(invalido))
                .isInstanceOf(InvalidFormException.class);

        verify(veiculoRepository, never()).salvar(any());
    }

    @Test
    void salvar_deveLancarExcecao_quandoCapacidadeNula() {
        VeiculoData invalido = new VeiculoData("ABC1D23", "Scania R450", "Scania", 2022, null, true);

        assertThatThrownBy(() -> service.salvar(invalido))
                .isInstanceOf(InvalidFormException.class);

        verify(veiculoRepository, never()).salvar(any());
    }

    @Test
    void listarTodos_deveRetornarLista() {
        when(veiculoRepository.pesquisar(null, null)).thenReturn(List.of(veiculoMock));

        List<Veiculo> resultado = service.pesquisar(null, null);

        assertThat(resultado).hasSize(1).contains(veiculoMock);
    }

    @Test
    void listarTodos_deveRetornarListaVazia_quandoNenhumVeiculo() {
        when(veiculoRepository.pesquisar(null, null)).thenReturn(List.of());

        assertThat(service.pesquisar(null, null)).isEmpty();
    }

    @Test
    void validate_naoDeveLancarExcecao_quandoVeiculoExiste() throws ResourceNotFoundException {
        when(veiculoRepository.buscarPorId(1L)).thenReturn(Optional.of(veiculoMock));

        assertThatNoException().isThrownBy(() -> service.validate(1L));
    }

    @Test
    void validate_deveLancarExcecao_quandoVeiculoNaoEncontrado() {
        when(veiculoRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.validate(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}