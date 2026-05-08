package com.beeftracker.backend.pedidoCompra;

import com.beeftracker.backend.auth.models.metadata.Metadata;
import com.beeftracker.backend.base.exceptions.ResourceNotFoundException;
import com.beeftracker.backend.compras.pedidoCompra.models.LoteBruto;
import com.beeftracker.backend.compras.pedidoCompra.models.LoteBrutoData;
import com.beeftracker.backend.compras.pedidoCompra.models.PedidoCompra;
import com.beeftracker.backend.compras.pedidoCompra.models.PedidoCompraData;
import com.beeftracker.backend.compras.pedidoCompra.repositories.PedidoCompraRepository;
import com.beeftracker.backend.compras.pedidoCompra.service.PedidoCompraService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoCompraServiceTest {

    @Mock
    private PedidoCompraRepository repository;

    @InjectMocks
    private PedidoCompraService service;

    private PedidoCompra pedidoMock;
    private PedidoCompraData dataMock;

    @BeforeEach
    void setUp() {
        dataMock = new PedidoCompraData(
                1L,
                new BigDecimal("1500.00"),
                "RASCUNHO",
                "Observação teste",
                LocalDate.now(),
                LocalDate.now().plusDays(10));

        Metadata metadata = new Metadata(LocalDate.now(), LocalDate.now(), 1L, "token-abc");
        pedidoMock = new PedidoCompra(dataMock, metadata);
    }

    // ─── criar ────────────────────────────────────────────────────────────────

    @Test
    void criar_deveChamarRepositorio_quandoDadosValidos() {
        service.criar(dataMock);
        verify(repository, times(1)).salvar(dataMock);
    }

    @Test
    void criar_deveLancarExcecao_quandoFornecedorNulo() {
        PedidoCompraData invalido = new PedidoCompraData(null, new BigDecimal("100"), "RASCUNHO", null, null, null);
        assertThatThrownBy(() -> service.criar(invalido))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void criar_deveLancarExcecao_quandoValorZero() {
        PedidoCompraData invalido = new PedidoCompraData(1L, BigDecimal.ZERO, "RASCUNHO", null, null, null);
        assertThatThrownBy(() -> service.criar(invalido))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void criar_deveLancarExcecao_quandoDataEntregaNoPassado() {
        PedidoCompraData invalido = new PedidoCompraData(1L, new BigDecimal("100"), "RASCUNHO", null, null, LocalDate.now().minusDays(1));
        assertThatThrownBy(() -> service.criar(invalido))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ─── editar ───────────────────────────────────────────────────────────────

    @Test
    void editar_deveChamarRepositorio_quandoPedidoExiste() throws ResourceNotFoundException {
        when(repository.carregar(1L)).thenReturn(pedidoMock);
        service.editar(1L, dataMock);
        verify(repository, times(1)).editar(eq(1L), eq(dataMock));
    }

    @Test
    void editar_deveLancarExcecao_quandoPedidoNaoExiste() {
        when(repository.carregar(99L)).thenThrow(new EmptyResultDataAccessException(1));
        assertThatThrownBy(() -> service.editar(99L, dataMock))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── atualizarStatus ──────────────────────────────────────────────────────

    @Test
    void atualizarStatus_deveAtualizar_quandoTransicaoValida() throws ResourceNotFoundException {
        when(repository.carregar(1L)).thenReturn(pedidoMock); // status = RASCUNHO
        service.atualizarStatus(1L, "ENVIADO");
        verify(repository, times(1)).editarStatus(1L, "ENVIADO");
    }

    @Test
    void atualizarStatus_deveLancarExcecao_quandoTransicaoInvalida() {
        when(repository.carregar(1L)).thenReturn(pedidoMock); // status = RASCUNHO
        assertThatThrownBy(() -> service.atualizarStatus(1L, "FINALIZADO"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("RASCUNHO → FINALIZADO");
    }

    @Test
    void atualizarStatus_deveLancarExcecao_quandoStatusTerminal() {
        PedidoCompraData cancelado = new PedidoCompraData(1L, new BigDecimal("100"), "CANCELADO", null, null, null);
        PedidoCompra pedidoCancelado = new PedidoCompra(cancelado, new Metadata(LocalDate.now(), LocalDate.now(), 1L, "token"));

        when(repository.carregar(1L)).thenReturn(pedidoCancelado);
        assertThatThrownBy(() -> service.atualizarStatus(1L, "RASCUNHO"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("CANCELADO → RASCUNHO");
    }

    // ─── carregar ─────────────────────────────────────────────────────────────

    @Test
    void carregar_deveRetornarPedido_quandoExiste() throws ResourceNotFoundException {
        when(repository.carregar(1L)).thenReturn(pedidoMock);
        PedidoCompra resultado = service.carregar(1L);
        assertThat(resultado).isEqualTo(pedidoMock);
    }

    @Test
    void carregar_deveLancarExcecao_quandoNaoExiste() {
        when(repository.carregar(99L)).thenThrow(new EmptyResultDataAccessException(1));
        assertThatThrownBy(() -> service.carregar(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── pesquisar ────────────────────────────────────────────────────────────

    @Test
    void pesquisar_deveRetornarLista() {
        when(repository.pesquisar(1L, "RASCUNHO", 0)).thenReturn(List.of(pedidoMock));
        List<PedidoCompra> resultado = service.pesquisar(1L, "RASCUNHO", 0);
        assertThat(resultado).hasSize(1);
    }

    @Test
    void pesquisar_deveRetornarListaVazia_quandoNenhumResultado() {
        when(repository.pesquisar(null, null, 0)).thenReturn(List.of());
        assertThat(service.pesquisar(null, null, 0)).isEmpty();
    }

    // ─── criarLote ────────────────────────────────────────────────────────────

    @Test
    void criarLote_deveChamarRepositorio_quandoDadosValidos() throws ResourceNotFoundException {
        when(repository.carregar(1L)).thenReturn(pedidoMock);
        LoteBrutoData loteData = new LoteBrutoData("Lote A", "Descrição", 500, 1L);
        service.criarLote(loteData);
        verify(repository, times(1)).salvarLote(loteData);
    }

    @Test
    void criarLote_deveLancarExcecao_quandoPedidoNaoExiste() {
        when(repository.carregar(99L)).thenThrow(new EmptyResultDataAccessException(1));
        LoteBrutoData loteData = new LoteBrutoData("Lote A", "Descrição", 500, 99L);
        assertThatThrownBy(() -> service.criarLote(loteData))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void criarLote_deveLancarExcecao_quandoNomeVazio() {
        when(repository.carregar(1L)).thenReturn(pedidoMock);
        LoteBrutoData invalido = new LoteBrutoData("", "Descrição", 500, 1L);
        assertThatThrownBy(() -> service.criarLote(invalido))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Nome do lote é obrigatório");
    }

    @Test
    void criarLote_deveLancarExcecao_quandoPesoZero() {
        when(repository.carregar(1L)).thenReturn(pedidoMock);
        LoteBrutoData invalido = new LoteBrutoData("Lote A", "Descrição", 0, 1L);
        assertThatThrownBy(() -> service.criarLote(invalido))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Peso do lote deve ser maior que zero");
    }

    @Test
    void editarLote_deveChamarRepositorio_quandoDadosValidos() throws ResourceNotFoundException {
        LoteBrutoData loteData = new LoteBrutoData("Lote A", "Descrição", 500, 1L);
        Metadata metadata = new Metadata(LocalDate.now(), LocalDate.now(), 1L, "token-lote");
        when(repository.carregarLote(1L)).thenReturn(new LoteBruto(loteData, metadata));

        service.editarLote(1L, loteData);
        verify(repository, times(1)).editarLote(eq(1L), eq(loteData));
    }

    @Test
    void editarLote_deveLancarExcecao_quandoLoteNaoExiste() {
        when(repository.carregarLote(99L)).thenThrow(new EmptyResultDataAccessException(1));
        LoteBrutoData loteData = new LoteBrutoData("Lote A", "Descrição", 500, 1L);
        assertThatThrownBy(() -> service.editarLote(99L, loteData))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── pesquisarLotes ───────────────────────────────────────────────────────

    @Test
    void pesquisarLotes_deveRetornarLista() throws ResourceNotFoundException {
        when(repository.carregar(1L)).thenReturn(pedidoMock);
        LoteBrutoData loteData = new LoteBrutoData("Lote A", "Descrição", 500, 1L);
        LoteBruto loteMock = new LoteBruto(loteData, new Metadata(LocalDate.now(), LocalDate.now(), 1L, "token-lote"));
        when(repository.pesquisarLotes(1L)).thenReturn(List.of(loteMock));

        assertThat(service.pesquisarLotes(1L)).hasSize(1);
    }

    @Test
    void pesquisarLotes_deveLancarExcecao_quandoPedidoNaoExiste() {
        when(repository.carregar(99L)).thenThrow(new EmptyResultDataAccessException(1));
        assertThatThrownBy(() -> service.pesquisarLotes(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}