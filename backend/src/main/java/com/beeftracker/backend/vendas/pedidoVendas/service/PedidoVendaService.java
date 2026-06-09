package com.beeftracker.backend.vendas.pedidoVendas.service;

import com.beeftracker.backend.base.exceptions.InvalidFormException;
import com.beeftracker.backend.base.exceptions.ResourceNotFoundException;
import com.beeftracker.backend.compras.pedidoCompra.models.LoteBruto;
import com.beeftracker.backend.compras.pedidoCompra.service.PedidoCompraService;
import com.beeftracker.backend.email.EmailClient;
import com.beeftracker.backend.vendas.pedidoVendas.models.LoteFracionado;
import com.beeftracker.backend.vendas.pedidoVendas.models.LoteFracionadoData;
import com.beeftracker.backend.vendas.pedidoVendas.models.PedidoVenda;
import com.beeftracker.backend.vendas.pedidoVendas.models.PedidoVendaData;
import com.beeftracker.backend.vendas.pedidoVendas.repositories.PedidoVendaRepository;
import com.beeftracker.backend.viagens.model.Viagem;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class PedidoVendaService {

    private final PedidoVendaRepository repository;
    private final PedidoCompraService compraService;
    public final EmailClient emailClient;
    @Value("${beeftracker.url}")
    private String beefTrackerUrl;

    public PedidoVendaService(PedidoVendaRepository repository, PedidoCompraService compraService, EmailClient emailClient) {
        this.repository = repository;
        this.compraService = compraService;
        this.emailClient = emailClient;
    }

    public void criar(PedidoVendaData data) {
        validarPedido(data);
        repository.salvar(data);
    }

    public void editar(Long id, PedidoVendaData data) throws ResourceNotFoundException {
        carregarOuLancarErro(id);
        validarPedido(data);
        repository.editar(id, data);
    }

    public void atualizarStatus(Long id, String novoStatus) throws ResourceNotFoundException {
        PedidoVenda pedido = carregarOuLancarErro(id);
        validarTransicaoStatus(pedido.data().status(), novoStatus);
        repository.editarStatus(id, novoStatus);
    }

    public PedidoVenda carregar(Long id) throws ResourceNotFoundException {
        return carregarOuLancarErro(id);
    }

    public List<PedidoVenda> pesquisar(Long clienteId, String status, int page) {
        return repository.pesquisar(clienteId, status, page);
    }

    public void criarLote(LoteFracionadoData data) throws ResourceNotFoundException, InvalidFormException {
        carregarOuLancarErro(data.pedidoVendaId());
        validarLote(data);

        LoteBruto loteBruto = compraService.carregarLoteOuLancarErro(data.loteOriginalId());

        if (loteBruto.data().peso() < data.peso()) {
            throw new InvalidFormException();
        }

        repository.decrementarPesoLoteBruto(data.loteOriginalId(), data.peso());
        repository.salvarLote(data);
    }

    public void editarLote(Long id, LoteFracionadoData data) throws ResourceNotFoundException {
        carregarLoteOuLancarErro(id);
        validarLote(data);
        repository.editarLote(id, data);
    }

    public LoteFracionado carregarLote(Long id) throws ResourceNotFoundException {
        return carregarLoteOuLancarErro(id);
    }

    public List<LoteFracionado> pesquisarLotes(Long pedidoVendaId) throws ResourceNotFoundException {
        carregarOuLancarErro(pedidoVendaId);
        return repository.pesquisarLotes(pedidoVendaId);
    }

    private PedidoVenda carregarOuLancarErro(Long id) throws ResourceNotFoundException {
        try {
            return repository.carregar(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException();
        }
    }

    private LoteFracionado carregarLoteOuLancarErro(Long id) throws ResourceNotFoundException {
        try {
            return repository.carregarLote(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException();
        }
    }

    private void validarPedido(PedidoVendaData data) {
        if (data.clienteId() == null) {
            throw new IllegalArgumentException("Cliente é obrigatório.");
        }
        if (data.valorTotal() == null || data.valorTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor total deve ser maior que zero.");
        }
        if (data.dataVencimento() != null && data.dataVencimento().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Data de vencimento não pode ser no passado.");
        }
    }

    private void validarLote(LoteFracionadoData data) {
        if (StringUtils.isBlank(data.nome())) {
            throw new IllegalArgumentException("Nome do lote é obrigatório.");
        }
        if (data.peso() == null || data.peso() <= 0) {
            throw new IllegalArgumentException("Peso do lote deve ser maior que zero.");
        }
    }

    private void validarTransicaoStatus(String statusAtual, String novoStatus) {
        Map<String, List<String>> transicoesPermitidas = Map.of(
                "PENDENTE", List.of("ENTREGUE", "CANCELADO"),
                "ENTREGUE", List.of(),
                "CANCELADO", List.of());

        List<String> permitidos = transicoesPermitidas.getOrDefault(statusAtual, List.of());
        if (!permitidos.contains(novoStatus)) {
            throw new IllegalStateException(
                    String.format("Transição de status inválida: %s → %s", statusAtual, novoStatus));
        }
    }

    public PedidoVenda findByViagem(Viagem viagem) {
        return repository.findByViagem(viagem.metadata().id());
    }

    public void enviarEmail(String nome, String to, String token) throws ResendException {
        String link = beefTrackerUrl + "/public/viagem/" + URLEncoder.encode(token, StandardCharsets.UTF_8);

        CreateEmailOptions email = CreateEmailOptions.builder()
                .from("CRM Frigorífico <suporte@beeftracker.xyz>")
                .to(to)
                .subject("Beef Tracker - Detalhes da sua Viagem")
                .html("""
                <html>
                  <body style="font-family: Arial, sans-serif; padding: 20px;">
                    <h2>Olá, %s!</h2>
                    <p>Os detalhes da sua viagem estão disponíveis.</p>
                    <p>Para visualizar as informações completas da viagem, clique no link abaixo:</p>
                    <a href="%s">Visualizar Detalhes da Viagem</a>
                    <br><br>
                    <small>Este é um e-mail automático enviado pelo CRM Beef Tracker.</small>
                  </body>
                </html>
            """.formatted(nome, link))
                .build();
        emailClient.enviarEmail(email);
    }
}