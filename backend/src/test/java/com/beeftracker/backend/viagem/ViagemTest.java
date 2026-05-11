package com.beeftracker.backend.viagem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.beeftracker.backend.auth.models.metadata.Metadata;
import com.beeftracker.backend.base.exceptions.InvalidFormException;
import com.beeftracker.backend.viagens.model.*;
import com.beeftracker.backend.viagens.repository.ViagemRepository;
import com.beeftracker.backend.viagens.service.ViagemService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ViagemTest {

    @Mock
    private ViagemRepository repository;

    @InjectMocks
    private ViagemService service;

    private ViagemData dataMock;
    private Viagem viagemMock;

    @BeforeEach
    void setUp() {
        dataMock = new ViagemData(
                1,
                2,
                "Entrega de gado — Fazenda São João",
                StatusViagem.PENDENTE,
                LocalDateTime.now(),
                null,
                LocalDateTime.now());

        Metadata metadata = new Metadata(LocalDate.now(), LocalDate.now(), 1L, "token-mock");
        viagemMock = new Viagem(dataMock, metadata);
    }

    // ----------------------------------------------------------------
    // criar()
    // ----------------------------------------------------------------
    @Nested
    @DisplayName("criar()")
    class Criar {

        @Test
        @DisplayName("deve chamar repository.save() com os dados recebidos")
        void deveChamarSaveComOsDados() {
            service.criar(dataMock);

            verify(repository, times(1)).save(any(Viagem.class));
        }

        @Test
        @DisplayName("deve criar viagem com status PENDENTE por padrão")
        void deveCriarComStatusPendente() {
            service.criar(dataMock);

            verify(repository).save(argThat(v ->
                    v.data().statusViagem() == StatusViagem.PENDENTE));
        }

        @Test
        @DisplayName("deve lançar InvalidFormException quando descrição for blank")
        void deveLancarExcecaoQuandoDescricaoForBlank() {
            ViagemData dataInvalida = new ViagemData(
                    1, 2, "   ", StatusViagem.PENDENTE,
                    LocalDateTime.now(), null, LocalDateTime.now());

            assertThrows(InvalidFormException.class,
                    () -> service.criar(dataInvalida));

            verifyNoInteractions(repository);
        }

        @Test
        @DisplayName("deve lançar IllegalArgumentException quando data for nula")
        void deveLancarExcecaoQuandoDataForNula() {
            assertThrows(IllegalArgumentException.class,
                    () -> service.criar(null));

            verifyNoInteractions(repository);
        }
    }

    // ----------------------------------------------------------------
    // editar()
    // ----------------------------------------------------------------
    @Nested
    @DisplayName("editar()")
    class Editar {

        @Test
        @DisplayName("deve chamar repository.save() com os dados atualizados")
        void deveChamarSaveComDadosAtualizados() {
            when(repository.findById(1L)).thenReturn(Optional.of(viagemMock));

            service.editar(1L, dataMock);

            verify(repository, times(1)).save(any(Viagem.class));
        }

        @Test
        @DisplayName("não deve alterar o status ao editar")
        void naoDeveAlterarStatusAoEditar() {
            when(repository.findById(1L)).thenReturn(Optional.of(viagemMock));

            ViagemData novosDados = new ViagemData(
                    1, 2, "Nova descrição", StatusViagem.ENTREGUE,
                    LocalDateTime.now(), null, LocalDateTime.now());

            service.editar(1L, novosDados);

            verify(repository).save(argThat(v ->
                    v.data().statusViagem() == StatusViagem.PENDENTE));
        }

        @Test
        @DisplayName("deve lançar IllegalArgumentException para id inexistente")
        void deveLancarExcecaoParaIdInexistente() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                    () -> service.editar(99L, dataMock));

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar InvalidFormException quando descrição for blank")
        void deveLancarExcecaoQuandoDescricaoForBlank() {
            when(repository.findById(1L)).thenReturn(Optional.of(viagemMock));

            ViagemData dataInvalida = new ViagemData(
                    1, 2, "", StatusViagem.PENDENTE,
                    LocalDateTime.now(), null, LocalDateTime.now());

            assertThrows(InvalidFormException.class,
                    () -> service.editar(1L, dataInvalida));

            verify(repository, never()).save(any());
        }
    }

    // ----------------------------------------------------------------
    // alterarStatus()
    // ----------------------------------------------------------------
    @Nested
    @DisplayName("alterarStatus()")
    class AlterarStatus {

        @Test
        @DisplayName("deve alterar o status para EM_TRANSITO")
        void deveAlterarStatusParaEmTransito() {
            when(repository.findById(1L)).thenReturn(Optional.of(viagemMock));

            service.alterarStatus(1L, new NovoStatus("EM_TRANSITO"));

            verify(repository).save(argThat(v ->
                    v.data().statusViagem() == StatusViagem.EM_TRANSITO));
        }

        @Test
        @DisplayName("deve alterar o status para ENTREGUE")
        void deveAlterarStatusParaEntregue() {
            when(repository.findById(1L)).thenReturn(Optional.of(viagemMock));

            service.alterarStatus(1L, new NovoStatus("ENTREGUE"));

            verify(repository).save(argThat(v ->
                    v.data().statusViagem() == StatusViagem.ENTREGUE));
        }

        @Test
        @DisplayName("deve alterar o status para CANCELADA")
        void deveAlterarStatusParaCancelada() {
            when(repository.findById(1L)).thenReturn(Optional.of(viagemMock));

            service.alterarStatus(1L, new NovoStatus("CANCELADA"));

            verify(repository).save(argThat(v ->
                    v.data().statusViagem() == StatusViagem.CANCELADA));
        }

        @Test
        @DisplayName("deve lançar IllegalArgumentException para status inválido")
        void deveLancarExcecaoParaStatusInvalido() {
            when(repository.findById(1L)).thenReturn(Optional.of(viagemMock));

            assertThrows(IllegalArgumentException.class,
                    () -> service.alterarStatus(1L, new NovoStatus("INVALIDO")));

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar IllegalArgumentException para id inexistente")
        void deveLancarExcecaoParaIdInexistente() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                    () -> service.alterarStatus(99L, new NovoStatus("ENTREGUE")));

            verify(repository, never()).save(any());
        }
    }

    // ----------------------------------------------------------------
    // pesquisar()
    // ----------------------------------------------------------------
    @Nested
    @DisplayName("pesquisar()")
    class Pesquisar {

        @Test
        @DisplayName("deve retornar lista vazia quando repositório não tem resultados")
        void deveRetornarListaVaziaQuandoNaoHaViagens() {
            when(repository.findByStatus(any(), any())).thenReturn(List.of());

            List<Viagem> resultado = service.pesquisar("PENDENTE", 0);

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }

        @Test
        @DisplayName("deve repassar o status e a página ao repositório")
        void deveRepassarFiltrosAoRepositorio() {
            when(repository.findByStatus(any(), any())).thenReturn(List.of(viagemMock));

            service.pesquisar("PENDENTE", 0);

            verify(repository, times(1)).findByStatus(eq("PENDENTE"), any());
        }

        @Test
        @DisplayName("deve retornar as viagens encontradas pelo repositório")
        void deveRetornarViagensDoRepositorio() {
            when(repository.findByStatus(any(), any())).thenReturn(List.of(viagemMock));

            List<Viagem> resultado = service.pesquisar("PENDENTE", 0);

            assertEquals(1, resultado.size());
            assertEquals(viagemMock, resultado.get(0));
        }

        @Test
        @DisplayName("deve lançar IllegalArgumentException para status inválido")
        void deveLancarExcecaoParaStatusInvalido() {
            assertThrows(IllegalArgumentException.class,
                    () -> service.pesquisar("STATUS_INVALIDO", 0));

            verifyNoInteractions(repository);
        }
    }
}
