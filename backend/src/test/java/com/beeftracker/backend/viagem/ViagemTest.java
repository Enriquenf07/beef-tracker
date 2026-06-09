package com.beeftracker.backend.viagem;

import com.beeftracker.backend.ativos.sensores.models.Sensor;
import com.beeftracker.backend.ativos.sensores.models.SensorData;
import com.beeftracker.backend.ativos.sensores.services.SensorService;
import com.beeftracker.backend.auth.models.metadata.Metadata;
import com.beeftracker.backend.base.exceptions.ResourceNotFoundException;
import com.beeftracker.backend.base.exceptions.SensorIndisponivelException;
import com.beeftracker.backend.usuarios.models.Role;
import com.beeftracker.backend.usuarios.models.RolesFull;
import com.beeftracker.backend.usuarios.services.UsuarioService;
import com.beeftracker.backend.veiculos.services.VeiculoService;
import com.beeftracker.backend.viagens.model.*;
import com.beeftracker.backend.viagens.repository.ViagemRepository;
import com.beeftracker.backend.viagens.service.ViagemService;
import com.beeftracker.backend.viagens.strategy.Cancelada;
import com.beeftracker.backend.viagens.strategy.EmTransito;
import com.beeftracker.backend.viagens.strategy.Entregue;
import com.influxdb.v3.client.InfluxDBClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    private VeiculoService veiculoService;
    private SensorService sensorService;
    private UsuarioService usuarioService;

    private Viagem viagemComStatus(StatusViagem status) {
        ViagemData data = new ViagemData(1L, 2L, "token-abc", "desc", status, null, null, null, null, null);
        return new Viagem(data, new Metadata(LocalDate.now(), LocalDate.now(), 1L, "token-abc"));
    }

    @BeforeEach
    void setUp() {
        repository = mock(ViagemRepository.class);
        transito = mock(EmTransito.class);
        concluida = mock(Entregue.class);
        cancelada = mock(Cancelada.class);
        veiculoService = mock(VeiculoService.class);
        sensorService = mock(SensorService.class);
        usuarioService = mock(UsuarioService.class);
        influxDBClient = mock(InfluxDBClient.class);
        service = new ViagemService(repository, transito, concluida, cancelada, usuarioService, influxDBClient,
                veiculoService, sensorService);
    }

    @Test
    void criar_deveSalvarViagemComStatusPendente() throws ResourceNotFoundException {
        ViagemData data = new ViagemData(1L, 2L, "", "desc", StatusViagem.CANCELADA, null, null, null, null, null);
        service.criar(data);

        verify(repository).criar(argThat(d -> d.statusViagem() == StatusViagem.PENDENTE));
    }

    @Test
    void criar_deveLancarExcecao_quandoVeiculoInvalido() throws ResourceNotFoundException {
        doThrow(new ResourceNotFoundException()).when(veiculoService).validate(1L);

        ViagemData data = new ViagemData(1L, 2L, "", "desc", StatusViagem.PENDENTE, null, null, null, null, null);

        assertThrows(ResourceNotFoundException.class, () -> service.criar(data));
        verify(repository, never()).criar(any());
    }

    @Test
    void criar_deveLancarExcecao_quandoSensorInvalido() throws ResourceNotFoundException {
        when(sensorService.carregar(2L)).thenThrow(new ResourceNotFoundException());

        ViagemData data = new ViagemData(1L, 2L, "", "desc", StatusViagem.PENDENTE, null, null, null, null, null);

        assertThrows(ResourceNotFoundException.class, () -> service.criar(data));
        verify(repository, never()).criar(any());
    }

    @Test
    void criar_deveIgnorarStatusInformadoEForcalPendente() throws ResourceNotFoundException {
        ViagemData data = new ViagemData(1L, 2L, "", "desc", StatusViagem.EM_TRANSITO, null, null, null, null, null);
        service.criar(data);

        verify(repository).criar(argThat(d -> d.statusViagem() == StatusViagem.PENDENTE));
    }

    @Test
    void editar_deveAtualizarDescricao() throws ResourceNotFoundException {
        Viagem viagem = viagemComStatus(StatusViagem.PENDENTE);
        when(repository.carregar(1L)).thenReturn(viagem);

        service.editar(1L, "nova descricao");

        verify(repository).editar(argThat(d -> "nova descricao".equals(d.descricao())), eq(1L));
    }

    @Test
    void editar_deveManterStatusOriginal() throws ResourceNotFoundException {
        Viagem viagem = viagemComStatus(StatusViagem.EM_TRANSITO);
        when(repository.carregar(1L)).thenReturn(viagem);

        service.editar(1L, "nova descricao");

        verify(repository).editar(argThat(d -> d.statusViagem() == StatusViagem.EM_TRANSITO), eq(1L));
    }

    @Test
    void editar_quandoNaoEncontrado_deveLancarException() throws ResourceNotFoundException {
        when(repository.carregar(99L)).thenThrow(new ResourceNotFoundException());

        assertThrows(ResourceNotFoundException.class, () -> service.editar(99L, "x"));
    }

    @Test
    void alterarStatus_deveUsarStrategiaEmTransito() throws ResourceNotFoundException, SensorIndisponivelException {
        Viagem viagem = viagemComStatus(StatusViagem.PENDENTE);
        Viagem atualizada = viagemComStatus(StatusViagem.EM_TRANSITO);

        when(repository.carregar(1L)).thenReturn(viagem);
        when(transito.alterarStatus(viagem)).thenReturn(atualizada);

        NovoStatus status = mock(NovoStatus.class);
        when(status.novoStatus()).thenReturn("EM_TRANSITO");

        service.alterarStatus(1L, status);

        verify(transito).alterarStatus(viagem);
        verify(repository).editar(atualizada.data(), 1L);
    }

    @Test
    void alterarStatus_deveUsarStrategiaEntregue() throws ResourceNotFoundException, SensorIndisponivelException {
        Viagem viagem = viagemComStatus(StatusViagem.EM_TRANSITO);
        Viagem atualizada = viagemComStatus(StatusViagem.ENTREGUE);

        when(repository.carregar(1L)).thenReturn(viagem);
        when(concluida.alterarStatus(viagem)).thenReturn(atualizada);

        NovoStatus status = mock(NovoStatus.class);
        when(status.novoStatus()).thenReturn("ENTREGUE");

        service.alterarStatus(1L, status);

        verify(concluida).alterarStatus(viagem);
        verify(repository).editar(atualizada.data(), 1L);
    }

    @Test
    void alterarStatus_deveUsarStrategiaCancelada() throws ResourceNotFoundException, SensorIndisponivelException {
        Viagem viagem = viagemComStatus(StatusViagem.PENDENTE);
        Viagem atualizada = viagemComStatus(StatusViagem.CANCELADA);

        when(repository.carregar(1L)).thenReturn(viagem);
        when(cancelada.alterarStatus(viagem)).thenReturn(atualizada);

        NovoStatus status = mock(NovoStatus.class);
        when(status.novoStatus()).thenReturn("CANCELADA");

        service.alterarStatus(1L, status);

        verify(cancelada).alterarStatus(viagem);
        verify(repository).editar(atualizada.data(), 1L);
    }

    @Test
    void alterarStatus_quandoNaoEncontrado_deveLancarException()
            throws ResourceNotFoundException, SensorIndisponivelException {
        when(repository.carregar(99L)).thenThrow(new ResourceNotFoundException());

        NovoStatus status = mock(NovoStatus.class);
        when(status.novoStatus()).thenReturn("EM_TRANSITO");

        assertThrows(ResourceNotFoundException.class, () -> service.alterarStatus(99L, status));
    }

    @Test
    void pesquisar_deveRepassarParametrosAoRepository() {
        RolesFull roles = new RolesFull(List.of(new Role("ADMIN", "", 1L)));
        when(usuarioService.getRoles(1L)).thenReturn(roles);

        service.pesquisar("PENDENTE", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), 1, 1L);

        verify(repository).findByStatusAndData("PENDENTE", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), 1,
                false, 1L);
    }

    @Test
    void pesquisar_quandoPageNulo_deveUsarPaginaUm() {
        RolesFull roles = new RolesFull(List.of(new Role("ADMIN", "", 1L)));
        when(usuarioService.getRoles(1L)).thenReturn(roles);

        service.pesquisar(null, null, null, null, 1L);

        verify(repository).findByStatusAndData(null, null, null, 1, false, 1L);
    }

    @Test
    void pesquisar_quandoPageZero_deveUsarPaginaUm() {
        RolesFull roles = new RolesFull(List.of(new Role("ADMIN", "", 1L)));
        when(usuarioService.getRoles(1L)).thenReturn(roles);

        service.pesquisar(null, null, null, 0, 1L);

        verify(repository).findByStatusAndData(null, null, null, 1, false, 1L);
    }

    @Test
    void pesquisar_quandoUsuarioEMotorista_deveFiltrarPorMotorista() {
        RolesFull roles = new RolesFull(List.of(new Role("MOTORISTA", "", 2L)));
        when(usuarioService.getRoles(5L)).thenReturn(roles);

        service.pesquisar(null, null, null, 1, 5L);

        verify(repository).findByStatusAndData(null, null, null, 1, true, 5L);
    }

    @Test
    void pesquisar_quandoUsuarioTemAdminEMotorista_naoDeveUsarFiltroMotorista() {
        RolesFull roles = new RolesFull(List.of(
                new Role("ADMIN", "", 1L),
                new Role("MOTORISTA", "", 2L)));
        when(usuarioService.getRoles(1L)).thenReturn(roles);

        service.pesquisar(null, null, null, 1, 1L);

        verify(repository).findByStatusAndData(null, null, null, 1, false, 1L);
    }

    @Test
    void criarLeitura_deveLancarExcecao_quandoNenhumaViagemAtivaParaSensor() {
        when(repository.findBySensorTokenAtiva("token-abc")).thenReturn(null);

        SensorLeitura leitura = new SensorLeitura("token-abc", null, -23.0, -46.0, 18.5, 65.0);

        assertThrows(IllegalStateException.class, () -> service.criarLeitura(leitura));
    }

    @Test
    void criarLeitura_deveLancarExcecao_quandoViagemNaoEstaEmTransito() {
        Viagem viagem = viagemComStatus(StatusViagem.PENDENTE);
        when(repository.findBySensorTokenAtiva("token-abc")).thenReturn(viagem);

        SensorLeitura leitura = new SensorLeitura("token-abc", null, -23.0, -46.0, 18.5, 65.0);

        assertThrows(IllegalStateException.class, () -> service.criarLeitura(leitura));
    }

    @Test
    void criarLeitura_deveLancarExcecao_quandoSaidaRealEmNula() {
        ViagemData data = new ViagemData(1L, 2L, "token-abc", "desc", StatusViagem.EM_TRANSITO,
                null, null, null, null, null);
        Viagem viagem = new Viagem(data, new Metadata(LocalDate.now(), LocalDate.now(), 1L, "token-abc"));
        when(repository.findBySensorTokenAtiva("token-abc")).thenReturn(viagem);

        SensorLeitura leitura = new SensorLeitura("token-abc", null, -23.0, -46.0, 18.5, 65.0);

        assertThrows(IllegalStateException.class, () -> service.criarLeitura(leitura));
    }

    @Test
    void criarLeitura_deveGravarNaInflux_quandoViagemValidaEmTransito() {
        ViagemData data = new ViagemData(1L, 2L, "token-abc", "desc", StatusViagem.EM_TRANSITO,
                null, LocalDateTime.now(), null, null, null);
        Viagem viagem = new Viagem(data, new Metadata(LocalDate.now(), LocalDate.now(), 1L, "token-abc"));
        when(repository.findBySensorTokenAtiva("token-abc")).thenReturn(viagem);

        SensorLeitura leitura = new SensorLeitura("token-abc", null, -23.0, -46.0, 18.5, 65.0);

        assertDoesNotThrow(() -> service.criarLeitura(leitura));
        verify(influxDBClient).writePoint(any());
    }

    @Test
    void getStats_deveLancarExcecao_quandoViagemNaoEncontrada() throws ResourceNotFoundException {
        when(repository.carregar(99L)).thenThrow(new ResourceNotFoundException());

        assertThrows(ResourceNotFoundException.class, () -> service.getStats(99L));
    }

    @Test
    void getStats_deveLancarExcecao_quandoViagemPendente() throws ResourceNotFoundException {
        Viagem viagem = viagemComStatus(StatusViagem.PENDENTE);
        when(repository.carregar(1L)).thenReturn(viagem);

        assertThrows(ResourceNotFoundException.class, () -> service.getStats(1L));
    }
}