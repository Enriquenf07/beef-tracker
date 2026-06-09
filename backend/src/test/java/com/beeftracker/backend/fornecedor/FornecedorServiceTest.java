package com.beeftracker.backend.fornecedor;

import com.beeftracker.backend.auth.models.metadata.Metadata;
import com.beeftracker.backend.base.exceptions.InvalidFormException;
import com.beeftracker.backend.base.exceptions.ResourceNotFoundException;
import com.beeftracker.backend.compras.fornecedores.models.Fornecedor;
import com.beeftracker.backend.compras.fornecedores.models.FornecedorData;
import com.beeftracker.backend.compras.fornecedores.repositories.FornecedorRepository;
import com.beeftracker.backend.compras.fornecedores.services.FornecedorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FornecedorServiceTest {

    @Mock
    private FornecedorRepository fornecedorRepository;

    @InjectMocks
    private FornecedorService service;

    private FornecedorData dataMock;
    private Fornecedor fornecedorMock;

    @BeforeEach
    void setUp() {
        dataMock = new FornecedorData("Fornecedor Teste", "FT", "12.345.678/0001-99", "01310-100", true,
                "Av. Paulista, 1000");
        Metadata metadata = new Metadata(LocalDate.now(), LocalDate.now(), 1L, "token-forn");
        fornecedorMock = new Fornecedor(dataMock, metadata);
    }

    @Test
    void cadastrar_deveChamarRepositorio_quandoDadosValidos() throws InvalidFormException {
        when(fornecedorRepository.salvar(dataMock)).thenReturn(1L);

        Long id = service.cadastrar(dataMock);

        verify(fornecedorRepository).salvar(dataMock);
        assertThat(id).isEqualTo(1L);
    }

    @Test
    void cadastrar_deveLancarExcecao_quandoNomeEmBranco() {
        FornecedorData invalido = new FornecedorData("", "FT", "12.345.678/0001-99", "01310-100", true, "Av. Paulista");

        assertThatThrownBy(() -> service.cadastrar(invalido))
                .isInstanceOf(InvalidFormException.class);

        verify(fornecedorRepository, never()).salvar(any());
    }

    @Test
    void cadastrar_deveLancarExcecao_quandoNomeNulo() {
        FornecedorData invalido = new FornecedorData(null, "FT", "12.345.678/0001-99", "01310-100", true,
                "Av. Paulista");

        assertThatThrownBy(() -> service.cadastrar(invalido))
                .isInstanceOf(InvalidFormException.class);

        verify(fornecedorRepository, never()).salvar(any());
    }

    @Test
    void atualizar_deveChamarRepositorio_quandoFornecedorExiste()
            throws ResourceNotFoundException, InvalidFormException {
        when(fornecedorRepository.buscarPorId(1L)).thenReturn(Optional.of(fornecedorMock));

        service.atualizar(1L, dataMock);

        verify(fornecedorRepository).atualizar(1L, dataMock);
    }

    @Test
    void atualizar_deveLancarExcecao_quandoFornecedorNaoExiste() {
        when(fornecedorRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.atualizar(99L, dataMock))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(fornecedorRepository, never()).atualizar(any(), any());
    }

    @Test
    void atualizar_deveLancarExcecao_quandoNomeEmBranco() {
        when(fornecedorRepository.buscarPorId(1L)).thenReturn(Optional.of(fornecedorMock));
        FornecedorData invalido = new FornecedorData("", "FT", "12.345.678/0001-99", "01310-100", true, "Av. Paulista");

        assertThatThrownBy(() -> service.atualizar(1L, invalido))
                .isInstanceOf(InvalidFormException.class);

        verify(fornecedorRepository, never()).atualizar(any(), any());
    }

    @Test
    void atualizarStatus_deveDesativar_quandoFornecedorAtivo() throws ResourceNotFoundException {
        when(fornecedorRepository.buscarPorId(1L)).thenReturn(Optional.of(fornecedorMock));

        service.atualizarStatus(1L);

        verify(fornecedorRepository).atualizarStatus(1L, false);
    }

    @Test
    void atualizarStatus_deveAtivar_quandoFornecedorInativo() throws ResourceNotFoundException {
        FornecedorData inativo = new FornecedorData("Fornecedor", "FT", "12.345.678/0001-99", "01310-100", false,
                "Av. Paulista");
        Fornecedor fornecedorInativo = new Fornecedor(inativo, new Metadata(LocalDate.now(), LocalDate.now(), 2L, "t"));
        when(fornecedorRepository.buscarPorId(2L)).thenReturn(Optional.of(fornecedorInativo));

        service.atualizarStatus(2L);

        verify(fornecedorRepository).atualizarStatus(2L, true);
    }

    @Test
    void atualizarStatus_deveLancarExcecao_quandoFornecedorNaoExiste() {
        when(fornecedorRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.atualizarStatus(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(fornecedorRepository, never()).atualizarStatus(any(), any());
    }

    @Test
    void findById_deveRetornarFornecedor_quandoExiste() throws ResourceNotFoundException {
        when(fornecedorRepository.buscarPorId(1L)).thenReturn(Optional.of(fornecedorMock));

        Fornecedor resultado = service.findById(1L);

        assertThat(resultado).isEqualTo(fornecedorMock);
    }

    @Test
    void findById_deveLancarExcecao_quandoNaoExiste() {
        when(fornecedorRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void pesquisar_deveRetornarListaOrdenadaPorId() {
        FornecedorData data2 = new FornecedorData("Alfa", "A", "11.111.111/0001-11", "01310-100", true, "Rua A");
        Fornecedor f2 = new Fornecedor(data2, new Metadata(LocalDate.now(), LocalDate.now(), 2L, "t2"));

        when(fornecedorRepository.pesquisar(null, true)).thenReturn(List.of(f2, fornecedorMock));

        List<Fornecedor> resultado = service.pesquisar(null, true);

        assertThat(resultado).extracting(f -> f.metadata().id()).containsExactly(1L, 2L);
    }

    @Test
    void pesquisar_deveRetornarListaVazia_quandoNenhumResultado() {
        when(fornecedorRepository.pesquisar("inexistente", null)).thenReturn(List.of());

        assertThat(service.pesquisar("inexistente", null)).isEmpty();
    }
}