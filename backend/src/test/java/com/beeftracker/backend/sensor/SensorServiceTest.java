package com.beeftracker.backend.sensor;

import com.beeftracker.backend.ativos.sensores.models.Sensor;
import com.beeftracker.backend.ativos.sensores.models.SensorData;
import com.beeftracker.backend.ativos.sensores.repositories.SensorRepository;
import com.beeftracker.backend.ativos.sensores.services.SensorService;
import com.beeftracker.backend.auth.models.metadata.Metadata;
import com.beeftracker.backend.base.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SensorServiceTest {

    @Mock
    private SensorRepository repository;

    @InjectMocks
    private SensorService service;

    private SensorData dataMock;
    private Sensor sensorMock;

    @BeforeEach
    void setUp() {
        dataMock = new SensorData("Sensor Câmara Fria", true);
        Metadata metadata = new Metadata(LocalDate.now(), LocalDate.now(), 1L, "token-sensor");
        sensorMock = new Sensor(dataMock, metadata);
    }

    @Test
    void cadastrar_deveChamarRepositorio() {
        service.cadastrar(dataMock);
        verify(repository).salvar(dataMock);
    }

    @Test
    void carregar_deveRetornarSensor_quandoExiste() throws ResourceNotFoundException {
        when(repository.carregar(1L)).thenReturn(sensorMock);

        Sensor resultado = service.carregar(1L);

        assertThat(resultado).isEqualTo(sensorMock);
    }

    @Test
    void carregar_deveLancarExcecao_quandoRetornaNull() {
        when(repository.carregar(99L)).thenReturn(null);

        assertThatThrownBy(() -> service.carregar(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void editar_deveChamarRepositorio_quandoSensorExiste() throws ResourceNotFoundException {
        when(repository.carregar(1L)).thenReturn(sensorMock);

        service.editar(1L, dataMock);

        verify(repository).editar(1L, dataMock);
    }

    @Test
    void editar_deveLancarExcecao_quandoSensorNaoExiste() {
        when(repository.carregar(99L)).thenReturn(null);

        assertThatThrownBy(() -> service.editar(99L, dataMock))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(repository, never()).editar(any(), any());
    }

    @Test
    void editarStatus_deveDesativar_quandoSensorAtivo() throws ResourceNotFoundException {
        when(repository.carregar(1L)).thenReturn(sensorMock);

        service.editarStatus(1L);

        verify(repository).editarStatus(1L, false);
    }

    @Test
    void editarStatus_deveAtivar_quandoSensorInativo() throws ResourceNotFoundException {
        SensorData inativo = new SensorData("Sensor Inativo", false);
        Sensor sensorInativo = new Sensor(inativo, new Metadata(LocalDate.now(), LocalDate.now(), 2L, "t"));
        when(repository.carregar(2L)).thenReturn(sensorInativo);

        service.editarStatus(2L);

        verify(repository).editarStatus(2L, true);
    }

    @Test
    void editarStatus_deveLancarExcecao_quandoSensorNaoExiste() {
        when(repository.carregar(99L)).thenReturn(null);

        assertThatThrownBy(() -> service.editarStatus(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(repository, never()).editarStatus(any(), anyBoolean());
    }

    @Test
    void pesquisar_deveRetornarLista() {
        when(repository.pesquisar(null, true)).thenReturn(List.of(sensorMock));

        assertThat(service.pesquisar(null, true)).hasSize(1).contains(sensorMock);
    }

    @Test
    void pesquisar_deveRetornarListaVazia_quandoNenhumResultado() {
        when(repository.pesquisar("inexistente", null)).thenReturn(List.of());

        assertThat(service.pesquisar("inexistente", null)).isEmpty();
    }

    @Test
    void listAll_deveRepassarFiltrosNulos() {
        when(repository.pesquisar(null, null)).thenReturn(List.of(sensorMock));

        assertThat(service.listAll()).hasSize(1);
        verify(repository).pesquisar(null, null);
    }
}