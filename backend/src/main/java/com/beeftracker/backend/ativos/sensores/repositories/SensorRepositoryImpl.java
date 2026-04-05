package com.beeftracker.backend.ativos.sensores.repositories;

import java.time.LocalDate;
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
                (rs, rowNum) -> new Sensor(
                        new SensorData(rs.getString("descricao"), rs.getBoolean("ativo")),
                        new Metadata(
                                rs.getObject("criado_em", LocalDate.class),
                                rs.getObject("atualizado_em", LocalDate.class),
                                rs.getLong("id"),
                                rs.getString("token"))));
    }

    @Override
    public List<Sensor> pesquisar(String chave, Boolean status) {
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
        sql.append("LIMIT :limit OFFSET :offset");

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("chave", StringUtils.isNotBlank(chave) ? "%" + chave.toLowerCase() + "%" : null)
                .addValue("status", status)
                .addValue("limit", 10)
                .addValue("offset", 0);

        return jdbcTemplate.query(sql.toString(), params, (rs, rowNum) -> new Sensor(
                new SensorData(rs.getString("descricao"), rs.getBoolean("ativo")),
                new Metadata(
                        rs.getObject("criado_em", LocalDate.class),
                        rs.getObject("atualizado_em", LocalDate.class),
                        rs.getLong("id"),
                        rs.getString("token"))));
    }
}
