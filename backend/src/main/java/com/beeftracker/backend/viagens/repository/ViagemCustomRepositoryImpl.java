package com.beeftracker.backend.viagens.repository;

import com.beeftracker.backend.viagens.model.StatusViagem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.beeftracker.backend.auth.models.metadata.Metadata;
import com.beeftracker.backend.viagens.model.Viagem;
import com.beeftracker.backend.viagens.model.ViagemData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Repository
public class ViagemCustomRepositoryImpl implements ViagemCustomRepository {

    private final JdbcTemplate jdbcTemplate;

    public ViagemCustomRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void criar(ViagemData viagem) {
        String sql = "INSERT INTO viagem (veiculo_id, sensor_id, descricao, status_viagem, saida_em) VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                viagem.veiculoId(),
                viagem.sensorId(),
                viagem.descricao(),
                viagem.statusViagem().name(),
                viagem.saidaEm());
    }

    @Override
    public void editar(ViagemData viagem, Long id) {
        String sql = """
                    UPDATE viagem SET
                        veiculo_id = ?,
                        sensor_id = ?,
                        descricao = ?,
                        status_viagem = ?,
                        entregue_em = ?
                    WHERE id = ?
                """;

        jdbcTemplate.update(sql,
                viagem.veiculoId(),
                viagem.sensorId(),
                viagem.descricao(),
                viagem.statusViagem().name(),
                viagem.entregueEm(),
                id);

    }

    @Override
    public Viagem carregar(Long id) {
        String sql = "SELECT v.veiculo_id, v.sensor_id, v.descricao, v.status_viagem, v.saida_em, v.entregue_em, v.atualizado_em, v.criado_em, v.id, v.token, s.token AS sensor_token FROM viagem v LEFT JOIN sensor s ON s.id = v.sensor_id WHERE v.id = ?";

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapRow(rs), id);
    }

    private Viagem mapRow(ResultSet rs) throws SQLException {
        return new Viagem(
                mapViagemData(rs),
                mapMetadata(rs)
        );
    }

    private ViagemData mapViagemData(ResultSet rs) throws SQLException {
        return new ViagemData(
                rs.getLong("veiculo_id"),
                rs.getLong("sensor_id"),
                rs.getString("sensor_token"),
                rs.getString("descricao"),
                StatusViagem.valueOf(rs.getString("status_viagem")),
                rs.getObject("saida_em", OffsetDateTime.class) != null
                        ? rs.getObject("saida_em", OffsetDateTime.class).toLocalDateTime() : null,
                rs.getObject("entregue_em", OffsetDateTime.class) != null
                        ? rs.getObject("entregue_em", OffsetDateTime.class).toLocalDateTime() : null,
                rs.getObject("atualizado_em", OffsetDateTime.class) != null
                        ? rs.getObject("atualizado_em", OffsetDateTime.class).toLocalDateTime() : null
        );
    }

    private Metadata mapMetadata(ResultSet rs) throws SQLException {
        return new Metadata(
                rs.getObject("criado_em", OffsetDateTime.class) != null
                        ? rs.getObject("criado_em", OffsetDateTime.class).toLocalDate() : null,
                rs.getObject("atualizado_em", OffsetDateTime.class) != null
                        ? rs.getObject("atualizado_em", OffsetDateTime.class).toLocalDate() : null,
                rs.getLong("id"),
                rs.getString("token")
        );
    }
}
