package com.beeftracker.backend.vendas.pedidoVendas.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.beeftracker.backend.auth.models.metadata.Metadata;
import com.beeftracker.backend.vendas.pedidoVendas.models.LoteFracionado;
import com.beeftracker.backend.vendas.pedidoVendas.models.LoteFracionadoData;
import com.beeftracker.backend.vendas.pedidoVendas.models.PedidoVenda;
import com.beeftracker.backend.vendas.pedidoVendas.models.PedidoVendaData;

import io.micrometer.common.util.StringUtils;

@Component
public class PedidoVendaRepositoryImpl implements PedidoVendaRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PedidoVendaRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void salvar(PedidoVendaData data) {
        String sql = "INSERT INTO pedido_venda (cliente_id, valor_total, status, observacao, data_venda, data_vencimento) "
                +
                "VALUES (:clienteId, :valorTotal, :status, :observacao, :dataVenda, :dataVencimento)";

        jdbcTemplate.update(sql, new MapSqlParameterSource()
                .addValue("clienteId", data.clienteId())
                .addValue("valorTotal", data.valorTotal())
                .addValue("status", data.status() != null ? data.status() : "RASCUNHO")
                .addValue("observacao", data.observacao())
                .addValue("dataVenda", data.dataVenda() != null ? data.dataVenda() : LocalDateTime.now())
                .addValue("dataVencimento", data.dataVencimento()));
    }

    @Override
    public void editar(Long id, PedidoVendaData data) {
        String sql = "UPDATE pedido_venda SET " +
                "cliente_id = :clienteId, " +
                "valor_total = :valorTotal, " +
                "observacao = :observacao, " +
                "data_vencimento = :dataVencimento " +
                "WHERE id = :id";

        jdbcTemplate.update(sql, new MapSqlParameterSource()
                .addValue("clienteId", data.clienteId())
                .addValue("valorTotal", data.valorTotal())
                .addValue("observacao", data.observacao())
                .addValue("dataVencimento", data.dataVencimento())
                .addValue("id", id));
    }

    @Override
    public void editarStatus(Long id, String status) {
        String sql = "UPDATE pedido_venda SET status = :status WHERE id = :id";

        jdbcTemplate.update(sql, new MapSqlParameterSource()
                .addValue("status", status)
                .addValue("id", id));
    }

    @Override
    public PedidoVenda carregar(Long id) {
        String sql = "SELECT p.id, p.token, p.cliente_id, p.valor_total, p.status, " +
                "p.observacao, p.data_venda, p.data_vencimento, p.criado_em, p.atualizado_em " +
                "FROM pedido_venda p WHERE p.id = :id";

        return jdbcTemplate.queryForObject(sql,
                new MapSqlParameterSource().addValue("id", id),
                (rs, rowNum) -> mapRow(rs));
    }

    @Override
    public List<PedidoVenda> pesquisar(Long clienteId, String status, int page) {
        StringBuilder sql = new StringBuilder(
                "SELECT p.id, p.token, p.cliente_id, p.valor_total, p.status, " +
                        "p.observacao, p.data_venda, p.data_vencimento, p.criado_em, p.atualizado_em " +
                        "FROM pedido_venda p ");

        List<String> conditions = new ArrayList<>();

        if (clienteId != null) {
            conditions.add("p.cliente_id = :clienteId");
        }
        if (StringUtils.isNotBlank(status)) {
            conditions.add("p.status = :status");
        }

        if (!conditions.isEmpty()) {
            sql.append("WHERE ").append(String.join(" AND ", conditions)).append(" ");
        }

        sql.append("ORDER BY p.id ASC ");
        sql.append("LIMIT 10 OFFSET :offset");

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("clienteId", clienteId)
                .addValue("status", status)
                .addValue("offset", page * 10);

        return jdbcTemplate.query(sql.toString(), params, (rs, rowNum) -> mapRow(rs));
    }

    private PedidoVenda mapRow(ResultSet rs) throws SQLException {
        PedidoVendaData data = new PedidoVendaData(
                rs.getLong("cliente_id"),
                rs.getBigDecimal("valor_total"),
                rs.getString("status"),
                rs.getString("observacao"),
                rs.getObject("data_venda", LocalDateTime.class),
                rs.getObject("data_vencimento", LocalDate.class));

        Metadata metadata = new Metadata(
                rs.getObject("criado_em", LocalDate.class),
                rs.getObject("atualizado_em", LocalDate.class),
                rs.getLong("id"),
                rs.getString("token"));

        return new PedidoVenda(data, metadata);
    }

    @Override
    public void salvarLote(LoteFracionadoData data) {
        String sql = "INSERT INTO lote_fracionado (nome, descricao, peso, pedido_venda_id) " +
                "VALUES (:nome, :descricao, :peso, :pedidoVendaId)";

        jdbcTemplate.update(sql, new MapSqlParameterSource()
                .addValue("nome", data.nome())
                .addValue("descricao", data.descricao())
                .addValue("peso", data.peso())
                .addValue("pedidoVendaId", data.pedidoVendaId()));
    }

    @Override
    public void editarLote(Long id, LoteFracionadoData data) {
        String sql = "UPDATE lote_fracionado SET nome = :nome, descricao = :descricao, peso = :peso WHERE id = :id";

        jdbcTemplate.update(sql, new MapSqlParameterSource()
                .addValue("nome", data.nome())
                .addValue("descricao", data.descricao())
                .addValue("peso", data.peso())
                .addValue("id", id));
    }

    @Override
    public LoteFracionado carregarLote(Long id) {
        String sql = "SELECT id, token, nome, descricao, peso, pedido_venda_id, criado_em, atualizado_em " +
                "FROM lote_fracionado WHERE id = :id";

        return jdbcTemplate.queryForObject(sql,
                new MapSqlParameterSource().addValue("id", id),
                (rs, rowNum) -> mapLoteRow(rs));
    }

    @Override
    public List<LoteFracionado> pesquisarLotes(Long pedidoVendaId) {
        String sql = "SELECT id, token, nome, descricao, peso, pedido_venda_id, criado_em, atualizado_em " +
                "FROM lote_fracionado WHERE pedido_venda_id = :pedidoVendaId ORDER BY id ASC";

        return jdbcTemplate.query(sql,
                new MapSqlParameterSource().addValue("pedidoVendaId", pedidoVendaId),
                (rs, rowNum) -> mapLoteRow(rs));
    }

    private LoteFracionado mapLoteRow(ResultSet rs) throws SQLException {
        LoteFracionadoData data = new LoteFracionadoData(
                rs.getString("nome"),
                rs.getString("descricao"),
                rs.getInt("peso"),
                rs.getLong("pedido_venda_id"));

        Metadata metadata = new Metadata(
                rs.getObject("criado_em", LocalDate.class),
                rs.getObject("atualizado_em", LocalDate.class),
                rs.getLong("id"),
                rs.getString("token"));

        return new LoteFracionado(data, metadata);
    }
}