package com.beeftracker.backend.veiculos.repositories;

import com.beeftracker.backend.auth.models.metadata.Metadata;
import com.beeftracker.backend.veiculos.models.Veiculo;
import com.beeftracker.backend.veiculos.models.VeiculoData;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class VeiculoCustomRepositoryImpl implements VeiculoCustomRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Long salvar(VeiculoData data) {
        String sql = "INSERT INTO veiculo (placa, modelo, marca, ano, capacidade_carga, ativo) " +
                "VALUES (:placa, :modelo, :marca, :ano, :capacidadeCarga, :ativo)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, toParams(data), keyHolder, new String[] { "id" });
        return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
    }

    @Override
    public Optional<Veiculo> buscarPorPlaca(String placa) {
        String sql = "SELECT id, token, placa, modelo, marca, ano, capacidade_carga, ativo, criado_em, atualizado_em " +
                "FROM veiculo WHERE LOWER(placa) = LOWER(:placa)";
        List<Veiculo> result = jdbcTemplate.query(sql,
                new MapSqlParameterSource().addValue("placa", placa), this::mapRow);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public void editar(Long id, VeiculoData data) {
        String sql = "UPDATE veiculo SET placa = :placa, modelo = :modelo, marca = :marca, " +
                "ano = :ano, capacidade_carga = :capacidadeCarga, ativo = :ativo WHERE id = :id";
        jdbcTemplate.update(sql, toParams(data).addValue("id", id));
    }

    @Override
    public void alterarStatus(Long id, Boolean ativo) {
        String sql = "UPDATE veiculo SET ativo = :ativo WHERE id = :id";
        jdbcTemplate.update(sql, new MapSqlParameterSource()
                .addValue("ativo", ativo)
                .addValue("id", id));
    }

    @Override
    public void excluir(Long id) {
        jdbcTemplate.update("DELETE FROM veiculo WHERE id = :id",
                new MapSqlParameterSource().addValue("id", id));
    }

    @Override
    public Optional<Veiculo> buscarPorId(Long id) {
        String sql = "SELECT id, token, placa, modelo, marca, ano, capacidade_carga, ativo, criado_em, atualizado_em " +
                "FROM veiculo WHERE id = :id";
        List<Veiculo> result = jdbcTemplate.query(sql,
                new MapSqlParameterSource().addValue("id", id), this::mapRow);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public List<Veiculo> pesquisar(String chave, Boolean status) {
        StringBuilder sql = new StringBuilder(
                "SELECT id, token, placa, modelo, marca, ano, capacidade_carga, ativo, criado_em, atualizado_em FROM veiculo ");

        List<String> conditions = new ArrayList<>();

        if (StringUtils.isNotBlank(chave)) {
            conditions.add("(LOWER(placa) LIKE :chave OR LOWER(modelo) LIKE :chave OR LOWER(marca) LIKE :chave)");
        }
        if (status != null) {
            conditions.add("ativo = :status");
        }
        if (!conditions.isEmpty()) {
            sql.append("WHERE ").append(String.join(" AND ", conditions)).append(" ");
        }

        sql.append("ORDER BY id ASC");

        MapSqlParameterSource params = new MapSqlParameterSource();
        if (StringUtils.isNotBlank(chave)) {
            params.addValue("chave", "%" + chave.toLowerCase() + "%");
        }
        if (status != null) {
            params.addValue("status", status);
        }

        return jdbcTemplate.query(sql.toString(), params, this::mapRow);
    }

    private Veiculo mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        OffsetDateTime criadoOdt = rs.getObject("criado_em", OffsetDateTime.class);
        OffsetDateTime atualizadoOdt = rs.getObject("atualizado_em", OffsetDateTime.class);
        LocalDate criadoDate = (criadoOdt != null) ? criadoOdt.toLocalDate() : null;
        LocalDate atualizadoDate = (atualizadoOdt != null) ? atualizadoOdt.toLocalDate() : null;

        return new Veiculo(
                new VeiculoData(
                        rs.getString("placa"),
                        rs.getString("modelo"),
                        rs.getString("marca"),
                        rs.getInt("ano"),
                        rs.getBigDecimal("capacidade_carga"),
                        rs.getBoolean("ativo")),
                new Metadata(criadoDate, atualizadoDate, rs.getLong("id"), rs.getString("token")));
    }

    private MapSqlParameterSource toParams(VeiculoData data) {
        return new MapSqlParameterSource()
                .addValue("placa", data.placa())
                .addValue("modelo", data.modelo())
                .addValue("marca", data.marca())
                .addValue("ano", data.ano())
                .addValue("capacidadeCarga", data.capacidadeCarga())
                .addValue("ativo", data.ativo() != null ? data.ativo() : true);
    }
}