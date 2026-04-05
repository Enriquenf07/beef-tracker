package com.beeftracker.backend.compras.pedidoCompra.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.beeftracker.backend.auth.models.metadata.Metadata;
import com.beeftracker.backend.compras.pedidoCompra.models.PedidoCompraData;

import io.micrometer.common.util.StringUtils;

import com.beeftracker.backend.compras.pedidoCompra.models.LoteBruto;
import com.beeftracker.backend.compras.pedidoCompra.models.LoteBrutoData;
import com.beeftracker.backend.compras.pedidoCompra.models.PedidoCompra;

@Component
public class PedidoCompraRepositoryImpl implements PedidoCompraRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PedidoCompraRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void salvar(PedidoCompraData data) {
        String sql = "INSERT INTO pedido_compra (fornecedor_id, valor_total, status, observacao, data_emissao, data_entrega) "
                +
                "VALUES (:fornecedorId, :valorTotal, :status, :observacao, :dataEmissao, :dataEntrega)";

        jdbcTemplate.update(sql, new MapSqlParameterSource()
                .addValue("fornecedorId", data.fornecedorId())
                .addValue("valorTotal", data.valorTotal())
                .addValue("status", data.status() != null ? data.status() : "RASCUNHO")
                .addValue("observacao", data.observacao())
                .addValue("dataEmissao", data.dataEmissao() != null ? data.dataEmissao() : LocalDate.now())
                .addValue("dataEntrega", data.dataEntrega()));
    }

    @Override
    public void editar(Long id, PedidoCompraData data) {
        String sql = "UPDATE pedido_compra SET " +
                "fornecedor_id = :fornecedorId, " +
                "valor_total = :valorTotal, " +
                "observacao = :observacao, " +
                "data_entrega = :dataEntrega " +
                "WHERE id = :id";

        jdbcTemplate.update(sql, new MapSqlParameterSource()
                .addValue("fornecedorId", data.fornecedorId())
                .addValue("valorTotal", data.valorTotal())
                .addValue("observacao", data.observacao())
                .addValue("dataEntrega", data.dataEntrega())
                .addValue("id", id));
    }

    @Override
    public void editarStatus(Long id, String status) {
        String sql = "UPDATE pedido_compra SET status = :status WHERE id = :id";

        jdbcTemplate.update(sql, new MapSqlParameterSource()
                .addValue("status", status)
                .addValue("id", id));
    }

    @Override
    public PedidoCompra carregar(Long id) {
        String sql = "SELECT p.id, p.token, p.fornecedor_id, p.valor_total, p.status, " +
                "p.observacao, p.data_emissao, p.data_entrega, p.criado_em, p.atualizado_em " +
                "FROM pedido_compra p WHERE p.id = :id";

        return jdbcTemplate.queryForObject(sql,
                new MapSqlParameterSource().addValue("id", id),
                (rs, rowNum) -> mapRow(rs));
    }

    @Override
    public List<PedidoCompra> pesquisar(Long fornecedorId, String status) {
        StringBuilder sql = new StringBuilder(
                "SELECT p.id, p.token, p.fornecedor_id, p.valor_total, p.status, " +
                        "p.observacao, p.data_emissao, p.data_entrega, p.criado_em, p.atualizado_em " +
                        "FROM pedido_compra p ");

        List<String> conditions = new ArrayList<>();

        if (fornecedorId != null) {
            conditions.add("p.fornecedor_id = :fornecedorId");
        }
        if (StringUtils.isNotBlank(status)) {
            conditions.add("p.status = :status");
        }

        if (!conditions.isEmpty()) {
            sql.append("WHERE ").append(String.join(" AND ", conditions)).append(" ");
        }

        sql.append("ORDER BY p.id ASC ");
        sql.append("LIMIT :limit OFFSET :offset");

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("fornecedorId", fornecedorId)
                .addValue("status", status)
                .addValue("limit", 10)
                .addValue("offset", 0);

        return jdbcTemplate.query(sql.toString(), params, (rs, rowNum) -> mapRow(rs));
    }

    private PedidoCompra mapRow(ResultSet rs) throws SQLException {
        PedidoCompraData data = new PedidoCompraData(
                rs.getLong("fornecedor_id"),
                rs.getBigDecimal("valor_total"),
                rs.getString("status"),
                rs.getString("observacao"),
                rs.getObject("data_emissao", LocalDate.class),
                rs.getObject("data_entrega", LocalDate.class));
        Metadata metadata = new Metadata(
                rs.getObject("criado_em", LocalDate.class),
                rs.getObject("atualizado_em", LocalDate.class),
                rs.getLong("id"),
                rs.getString("token"));

        return new PedidoCompra(data, metadata);
    }

    @Override
public void salvarLote(LoteBrutoData data) {
    String sql = "INSERT INTO lote_bruto (nome, descricao, peso, pedido_compra_id) " +
                 "VALUES (:nome, :descricao, :peso, :pedidoCompraId)";

    jdbcTemplate.update(sql, new MapSqlParameterSource()
            .addValue("nome", data.nome())
            .addValue("descricao", data.descricao())
            .addValue("peso", data.peso())
            .addValue("pedidoCompraId", data.pedidoCompraId()));
}

@Override
public void editarLote(Long id, LoteBrutoData data) {
    String sql = "UPDATE lote_bruto SET nome = :nome, descricao = :descricao, peso = :peso WHERE id = :id";

    jdbcTemplate.update(sql, new MapSqlParameterSource()
            .addValue("nome", data.nome())
            .addValue("descricao", data.descricao())
            .addValue("peso", data.peso())
            .addValue("id", id));
}

@Override
public LoteBruto carregarLote(Long id) {
    String sql = "SELECT id, token, nome, descricao, peso, pedido_compra_id, criado_em, atualizado_em " +
                 "FROM lote_bruto WHERE id = :id";

    return jdbcTemplate.queryForObject(sql,
            new MapSqlParameterSource().addValue("id", id),
            (rs, rowNum) -> mapLoteRow(rs));
}

@Override
public List<LoteBruto> pesquisarLotes(Long pedidoCompraId) {
    String sql = "SELECT id, token, nome, descricao, peso, pedido_compra_id, criado_em, atualizado_em " +
                 "FROM lote_bruto WHERE pedido_compra_id = :pedidoCompraId ORDER BY id ASC";

    return jdbcTemplate.query(sql,
            new MapSqlParameterSource().addValue("pedidoCompraId", pedidoCompraId),
            (rs, rowNum) -> mapLoteRow(rs));
}

private LoteBruto mapLoteRow(ResultSet rs) throws SQLException {
    LoteBrutoData data = new LoteBrutoData(
            rs.getString("nome"),
            rs.getString("descricao"),
            rs.getInt("peso"),
            rs.getLong("pedido_compra_id")
    );

    Metadata metadata = new Metadata(
            rs.getObject("criado_em", LocalDate.class),
            rs.getObject("atualizado_em", LocalDate.class),
            rs.getLong("id"),
            rs.getString("token")
    );

    return new LoteBruto(data, metadata);
}
}
