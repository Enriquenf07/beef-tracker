package com.beeftracker.backend.ativos.sensores.repositories;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.beeftracker.backend.ativos.sensores.models.Sensor;
import com.beeftracker.backend.ativos.sensores.models.SensorData;
import com.beeftracker.backend.auth.models.metadata.Metadata;

import io.micrometer.common.util.StringUtils;

@Repository
public class SensorRepositoryImpl implements SensorRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public SensorRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void salvar(SensorData data) {
        String sql = "INSERT INTO sensor (descricao, ativo) VALUES (:descricao, :ativo)";

        jdbcTemplate.update(sql, new MapSqlParameterSource()
                .addValue("descricao", data.descricao())
                .addValue("ativo", data.ativo() != null ? data.ativo() : true));
    }

    @Override
    public void editar(Long id, SensorData data) {
        String sql = "UPDATE sensor SET descricao = :descricao, ativo = :ativo WHERE id = :id";

        jdbcTemplate.update(sql, new MapSqlParameterSource()
                .addValue("descricao", data.descricao())
                .addValue("ativo", data.ativo())
                .addValue("id", id));
    }

    @Override
    public void editarStatus(Long id, boolean ativo) {
        String sql = "UPDATE sensor SET ativo = :ativo WHERE id = :id";

        jdbcTemplate.update(sql, new MapSqlParameterSource()
                .addValue("ativo", ativo)
                .addValue("id", id));
    }

    @Override
    public Sensor carregar(Long id) {
        String sql = "SELECT id, token, descricao, ativo, criado_em, atualizado_em FROM sensor WHERE id = :id";

        return jdbcTemplate.queryForObject(sql,
                new MapSqlParameterSource().addValue("id", id),
                (rs, rowNum) -> {
                    OffsetDateTime criadoOdt = rs.getObject("criado_em", OffsetDateTime.class);
                    OffsetDateTime atualizadoOdt = rs.getObject("atualizado_em", OffsetDateTime.class);

                    LocalDate criadoDate = (criadoOdt != null) ? criadoOdt.toLocalDate() : null;
                    LocalDate atualizadoDate = (atualizadoOdt != null) ? atualizadoOdt.toLocalDate() : null;

                    return new Sensor(
                            new SensorData(rs.getString("descricao"), rs.getBoolean("ativo")),
                            new Metadata(
                                    criadoDate,
                                    atualizadoDate,
                                    rs.getLong("id"),
                                    rs.getString("token")));
                });
    }

    @Override
    public List<Sensor> pesquisar(String chave, Boolean status) {
        int limit = 10;
        int offset = 0;

        StringBuilder sql = new StringBuilder(
                "SELECT id, token, descricao, ativo, criado_em, atualizado_em FROM sensor ");

        List<String> conditions = new ArrayList<>();

        if (StringUtils.isNotBlank(chave)) {
            conditions.add("LOWER(descricao) LIKE :chave");
        }

        if (status != null) {
            conditions.add("ativo = :status");
        }

        if (!conditions.isEmpty()) {
            sql.append("WHERE ").append(String.join(" AND ", conditions)).append(" ");
        }

        sql.append("ORDER BY id ASC ");
        sql.append("LIMIT ").append(limit).append(" OFFSET ").append(offset);

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (StringUtils.isNotBlank(chave)) {
            params.addValue("chave", "%" + chave.toLowerCase() + "%");
        }
        if (status != null) {
            params.addValue("status", status);
        }

        return jdbcTemplate.query(sql.toString(), params, (rs, rowNum) -> {
            OffsetDateTime criadoOdt = rs.getObject("criado_em", OffsetDateTime.class);
            OffsetDateTime atualizadoOdt = rs.getObject("atualizado_em", OffsetDateTime.class);

            LocalDate criadoDate = (criadoOdt != null) ? criadoOdt.toLocalDate() : null;
            LocalDate atualizadoDate = (atualizadoOdt != null) ? atualizadoOdt.toLocalDate() : null;

            return new Sensor(
                    new SensorData(rs.getString("descricao"), rs.getBoolean("ativo")),
                    new Metadata(
                            criadoDate,
                            atualizadoDate,
                            rs.getLong("id"),
                            rs.getString("token")));
        });
    }

    @Override
    public void excluir(Long id) {
        jdbcTemplate.update("DELETE FROM sensor WHERE id = :id",
                new MapSqlParameterSource().addValue("id", id));
    }
}