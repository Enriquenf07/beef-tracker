package com.beeftracker.backend.cliente;

import com.beeftracker.backend.auth.models.metadata.Metadata;
import com.beeftracker.backend.base.exceptions.InvalidFormException;
import com.beeftracker.backend.base.exceptions.ResourceNotFoundException;
import com.beeftracker.backend.vendas.clientes.models.Cliente;
import com.beeftracker.backend.vendas.clientes.models.ClienteData;
import com.beeftracker.backend.vendas.clientes.repositories.ClienteRepository;
import com.beeftracker.backend.vendas.clientes.services.ClienteService;
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
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService service;

    private ClienteData dataMock;
    private Cliente clienteMock;

    @BeforeEach
    void setUp() {
        dataMock = new ClienteData("Cliente Teste", "CT", "123.456.789-00", "cliente@teste.com", "(11) 99999-9999",
                "01310-100", "SP", true);
        Metadata metadata = new Metadata(LocalDate.now(), LocalDate.now(), 1L, "token-cli");
        clienteMock = new Cliente(dataMock, metadata);
    }

    @Test
    void cadastrar_deveChamarRepositorio_quandoDadosValidos() throws InvalidFormException {
        when(clienteRepository.salvar(dataMock)).thenReturn(1L);

        Long id = service.cadastrar(dataMock);

        verify(clienteRepository).salvar(dataMock);
        assertThat(id).isEqualTo(1L);
    }

    @Test
    void cadastrar_deveLancarExcecao_quandoNomeEmBranco() {
        ClienteData invalido = new ClienteData("", "CT", "123.456.789-00", "cliente@teste.com", "(11) 99999-9999",
                "01310-100", "SP", true);

        assertThatThrownBy(() -> service.cadastrar(invalido))
                .isInstanceOf(InvalidFormException.class);

        verify(clienteRepository, never()).salvar(any());
    }

    @Test
    void cadastrar_deveLancarExcecao_quandoNomeNulo() {
        ClienteData invalido = new ClienteData(null, "CT", "123.456.789-00", "cliente@teste.com", "(11) 99999-9999",
                "01310-100", "SP", true);

        assertThatThrownBy(() -> service.cadastrar(invalido))
                .isInstanceOf(InvalidFormException.class);

        verify(clienteRepository, never()).salvar(any());
    }

    @Test
    void atualizar_deveChamarRepositorio_quandoClienteExiste() throws ResourceNotFoundException, InvalidFormException {
        when(clienteRepository.buscarPorId(1L)).thenReturn(Optional.of(clienteMock));

        service.atualizar(1L, dataMock);

        verify(clienteRepository).atualizar(1L, dataMock);
    }

    @Test
    void atualizar_deveLancarExcecao_quandoClienteNaoExiste() {
        when(clienteRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.atualizar(99L, dataMock))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(clienteRepository, never()).atualizar(any(), any());
    }

    @Test
    void atualizar_deveLancarExcecao_quandoNomeEmBranco() {
        when(clienteRepository.buscarPorId(1L)).thenReturn(Optional.of(clienteMock));
        ClienteData invalido = new ClienteData("", "CT", "123.456.789-00", "cliente@teste.com", "(11) 99999-9999",
                "01310-100", "SP", true);

        assertThatThrownBy(() -> service.atualizar(1L, invalido))
                .isInstanceOf(InvalidFormException.class);

        verify(clienteRepository, never()).atualizar(any(), any());
    }

    @Test
    void atualizarStatus_deveDesativar_quandoClienteAtivo() throws ResourceNotFoundException {
        when(clienteRepository.buscarPorId(1L)).thenReturn(Optional.of(clienteMock));

        service.atualizarStatus(1L);

        verify(clienteRepository).atualizarStatus(1L, false);
    }

    @Test
    void atualizarStatus_deveAtivar_quandoClienteInativo() throws ResourceNotFoundException {
        ClienteData inativo = new ClienteData("Cliente", "CT", "123.456.789-00", "c@c.com", "(11) 99999-9999",
                "01310-100", "SP", false);
        Cliente clienteInativo = new Cliente(inativo, new Metadata(LocalDate.now(), LocalDate.now(), 2L, "t"));
        when(clienteRepository.buscarPorId(2L)).thenReturn(Optional.of(clienteInativo));

        service.atualizarStatus(2L);

        verify(clienteRepository).atualizarStatus(2L, true);
    }

    @Test
    void atualizarStatus_deveLancarExcecao_quandoClienteNaoExiste() {
        when(clienteRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.atualizarStatus(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(clienteRepository, never()).atualizarStatus(any(), any());
    }

    @Test
    void findById_deveRetornarCliente_quandoExiste() throws ResourceNotFoundException {
        when(clienteRepository.buscarPorId(1L)).thenReturn(Optional.of(clienteMock));

        Cliente resultado = service.findById(1L);

        assertThat(resultado).isEqualTo(clienteMock);
    }

    @Test
    void findById_deveLancarExcecao_quandoNaoExiste() {
        when(clienteRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void pesquisar_deveRetornarListaOrdenadaPorId() {
        ClienteData data2 = new ClienteData("Alfa", "A", "000.000.000-00", "a@a.com", "(11) 00000-0000", "01310-100",
                "SP", true);
        Cliente c2 = new Cliente(data2, new Metadata(LocalDate.now(), LocalDate.now(), 2L, "t2"));

        when(clienteRepository.pesquisar(null, true)).thenReturn(List.of(c2, clienteMock));

        List<Cliente> resultado = service.pesquisar(null, true);

        assertThat(resultado).extracting(c -> c.metadata().id()).containsExactly(1L, 2L);
    }

    @Test
    void pesquisar_deveRetornarListaVazia_quandoNenhumResultado() {
        when(clienteRepository.pesquisar("inexistente", null)).thenReturn(List.of());

        assertThat(service.pesquisar("inexistente", null)).isEmpty();
    }
}