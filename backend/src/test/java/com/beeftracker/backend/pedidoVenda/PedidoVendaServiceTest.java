package com.beeftracker.backend.pedidoVenda;

import com.beeftracker.backend.auth.models.metadata.Metadata;
import com.beeftracker.backend.base.exceptions.InvalidFormException;
import com.beeftracker.backend.base.exceptions.ResourceNotFoundException;
import com.beeftracker.backend.compras.pedidoCompra.models.LoteBruto;
import com.beeftracker.backend.compras.pedidoCompra.models.LoteBrutoData;
import com.beeftracker.backend.compras.pedidoCompra.service.PedidoCompraService;
import com.beeftracker.backend.email.EmailClient;
import com.beeftracker.backend.vendas.pedidoVendas.models.LoteFracionado;
import com.beeftracker.backend.vendas.pedidoVendas.models.LoteFracionadoData;
import com.beeftracker.backend.vendas.pedidoVendas.models.PedidoVenda;
import com.beeftracker.backend.vendas.pedidoVendas.models.PedidoVendaData;
import com.beeftracker.backend.vendas.pedidoVendas.repositories.PedidoVendaRepository;
import com.beeftracker.backend.vendas.pedidoVendas.service.PedidoVendaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoVendaServiceTest {

    @Mock
    private PedidoVendaRepository repository;

    @Mock
    private PedidoCompraService compraService;

    @Mock
    private EmailClient emailClient;

    @InjectMocks
    private PedidoVendaService service;

    private PedidoVendaData dataMock;
    private PedidoVenda pedidoMock;

    @BeforeEach
    void setUp() {
        dataMock = new PedidoVendaData(
                1L,
                new BigDecimal("2000.00"),
                "PENDENTE",
                "Observação",
                LocalDateTime.now(),
                LocalDate.now().plusDays(10));

        Metadata metadata = new Metadata(LocalDate.now(), LocalDate.now(), 1L, "token-venda");
        pedidoMock = new PedidoVenda(dataMock, metadata);
    }

    @Test
    void criar_deveChamarRepositorio_quandoDadosValidos() throws InvalidFormException {
        service.criar(dataMock);
        verify(repository, times(1)).salvar(dataMock);
    }



    @Test
    void criar_deveLancarExcecao_quandoValorNegativo() {
        PedidoVendaData invalido = new PedidoVendaData(1L, new BigDecimal("-1"), "PENDENTE", null, null, null);
        assertThatThrownBy(() -> service.criar(invalido))
                .isInstanceOf(InvalidFormException.class);
    }


    @Test
    void criar_naoDeveLancarExcecao_quandoDataVencimentoNula() {
        PedidoVendaData semVencimento = new PedidoVendaData(1L, new BigDecimal("100"), "PENDENTE", null, null, null);
        assertThatNoException().isThrownBy(() -> service.criar(semVencimento));
    }

    @Test
    void editar_deveChamarRepositorio_quandoPedidoExiste() throws ResourceNotFoundException, InvalidFormException {
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



    @Test
    void atualizarStatus_deveAtualizar_quandoPendenteParaEntregue() throws ResourceNotFoundException {
        when(repository.carregar(1L)).thenReturn(pedidoMock);
        service.atualizarStatus(1L, "ENTREGUE");
        verify(repository).editarStatus(1L, "ENTREGUE");
    }

    @Test
    void atualizarStatus_deveAtualizar_quandoPendenteParaCancelado() throws ResourceNotFoundException {
        when(repository.carregar(1L)).thenReturn(pedidoMock);
        service.atualizarStatus(1L, "CANCELADO");
        verify(repository).editarStatus(1L, "CANCELADO");
    }

    @Test
    void atualizarStatus_deveLancarExcecao_quandoEntregueParaQualquerStatus() {
        PedidoVendaData entregue = new PedidoVendaData(1L, new BigDecimal("100"), "ENTREGUE", null, null, null);
        PedidoVenda pedidoEntregue = new PedidoVenda(entregue, new Metadata(LocalDate.now(), LocalDate.now(), 1L, "t"));
        when(repository.carregar(1L)).thenReturn(pedidoEntregue);

        assertThatThrownBy(() -> service.atualizarStatus(1L, "PENDENTE"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ENTREGUE → PENDENTE");
    }

    @Test
    void atualizarStatus_deveLancarExcecao_quandoCanceladoParaQualquerStatus() {
        PedidoVendaData cancelado = new PedidoVendaData(1L, new BigDecimal("100"), "CANCELADO", null, null, null);
        PedidoVenda pedidoCancelado = new PedidoVenda(cancelado,
                new Metadata(LocalDate.now(), LocalDate.now(), 1L, "t"));
        when(repository.carregar(1L)).thenReturn(pedidoCancelado);

        assertThatThrownBy(() -> service.atualizarStatus(1L, "PENDENTE"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("CANCELADO → PENDENTE");
    }

    @Test
    void atualizarStatus_deveLancarExcecao_quandoPedidoNaoExiste() {
        when(repository.carregar(99L)).thenThrow(new EmptyResultDataAccessException(1));
        assertThatThrownBy(() -> service.atualizarStatus(99L, "ENTREGUE"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void carregar_deveRetornarPedido_quandoExiste() throws ResourceNotFoundException {
        when(repository.carregar(1L)).thenReturn(pedidoMock);
        PedidoVenda resultado = service.carregar(1L);
        assertThat(resultado).isEqualTo(pedidoMock);
    }

    @Test
    void carregar_deveLancarExcecao_quandoNaoExiste() {
        when(repository.carregar(99L)).thenThrow(new EmptyResultDataAccessException(1));
        assertThatThrownBy(() -> service.carregar(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void pesquisar_deveRetornarLista() {
        when(repository.pesquisar(1L, "PENDENTE", 0)).thenReturn(List.of(pedidoMock));
        assertThat(service.pesquisar(1L, "PENDENTE", 0)).hasSize(1);
    }

    @Test
    void pesquisar_deveRetornarListaVazia_quandoNenhumResultado() {
        when(repository.pesquisar(null, null, 0)).thenReturn(List.of());
        assertThat(service.pesquisar(null, null, 0)).isEmpty();
    }

    @Test
    void criarLote_deveChamarRepositorio_quandoDadosValidos() throws ResourceNotFoundException, InvalidFormException {
        LoteFracionadoData loteData = new LoteFracionadoData("Lote A", "Desc", 200, 1L, 10L);
        LoteBrutoData brutoData = new LoteBrutoData("Lote Bruto", "Desc", 500, 1L);
        LoteBruto loteBruto = new LoteBruto(brutoData, new Metadata(LocalDate.now(), LocalDate.now(), 10L, "t"));

        when(repository.carregar(1L)).thenReturn(pedidoMock);
        when(compraService.carregarLoteOuLancarErro(10L)).thenReturn(loteBruto);

        service.criarLote(loteData);

        verify(repository).decrementarPesoLoteBruto(10L, 200);
        verify(repository).salvarLote(loteData);
    }

    @Test
    void criarLote_deveLancarExcecao_quandoPesoFracionadoExcedeLoteBruto()
            throws ResourceNotFoundException {
        LoteFracionadoData loteData = new LoteFracionadoData("Lote A", "Desc", 600, 1L, 10L);
        LoteBrutoData brutoData = new LoteBrutoData("Lote Bruto", "Desc", 500, 1L);
        LoteBruto loteBruto = new LoteBruto(brutoData, new Metadata(LocalDate.now(), LocalDate.now(), 10L, "t"));

        when(repository.carregar(1L)).thenReturn(pedidoMock);
        when(compraService.carregarLoteOuLancarErro(10L)).thenReturn(loteBruto);

        assertThatThrownBy(() -> service.criarLote(loteData))
                .isInstanceOf(InvalidFormException.class);

        verify(repository, never()).salvarLote(any());
    }

    @Test
    void criarLote_deveLancarExcecao_quandoPedidoNaoExiste() {
        when(repository.carregar(99L)).thenThrow(new EmptyResultDataAccessException(1));
        LoteFracionadoData loteData = new LoteFracionadoData("Lote A", "Desc", 100, 99L, 10L);

        assertThatThrownBy(() -> service.criarLote(loteData))
                .isInstanceOf(ResourceNotFoundException.class);
    }


    @Test
    void editarLote_deveChamarRepositorio_quandoDadosValidos() throws ResourceNotFoundException, InvalidFormException {
        LoteFracionadoData loteData = new LoteFracionadoData("Lote A", "Desc", 200, 1L, 10L);
        LoteFracionado loteMock = new LoteFracionado(loteData, new Metadata(LocalDate.now(), LocalDate.now(), 1L, "t"));
        when(repository.carregarLote(1L)).thenReturn(loteMock);

        service.editarLote(1L, loteData);

        verify(repository).editarLote(eq(1L), eq(loteData));
    }

    @Test
    void editarLote_deveLancarExcecao_quandoLoteNaoExiste() {
        when(repository.carregarLote(99L)).thenThrow(new EmptyResultDataAccessException(1));
        LoteFracionadoData loteData = new LoteFracionadoData("Lote A", "Desc", 200, 1L, 10L);

        assertThatThrownBy(() -> service.editarLote(99L, loteData))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void pesquisarLotes_deveRetornarLista() throws ResourceNotFoundException {
        LoteFracionadoData loteData = new LoteFracionadoData("Lote A", "Desc", 200, 1L, 10L);
        LoteFracionado loteMock = new LoteFracionado(loteData, new Metadata(LocalDate.now(), LocalDate.now(), 1L, "t"));

        when(repository.carregar(1L)).thenReturn(pedidoMock);
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