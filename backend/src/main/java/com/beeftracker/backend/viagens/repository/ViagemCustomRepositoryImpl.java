package com.beeftracker.backend.viagens.repository;

import com.beeftracker.backend.base.Page;
import com.beeftracker.backend.base.exceptions.ResourceNotFoundException;
import com.beeftracker.backend.viagens.model.StatusViagem;
import org.springframework.dao.EmptyResultDataAccessException;
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
import java.util.ArrayList;
import java.util.List;

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
                        entregue_em = ?,
                        saida_real_em = ?
                    WHERE id = ?
                """;

        jdbcTemplate.update(sql,
                viagem.veiculoId(),
                viagem.sensorId(),
                viagem.descricao(),
                viagem.statusViagem().name(),
                viagem.entregueEm(),
                viagem.saidaRealEm(),
                id);

    }

    @Override
    public Viagem carregar(Long id) throws ResourceNotFoundException {
        String sql = "SELECT v.veiculo_id, v.sensor_id, v.descricao, v.status_viagem, v.saida_em, v.saida_real_em, v.entregue_em, v.atualizado_em, v.criado_em, v.id, v.token, s.token AS sensor_token, v.motorista_id FROM viagem v LEFT JOIN sensor s ON s.id = v.sensor_id WHERE v.id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapRow(rs), id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException();
        }
    }

        @Override
    public Viagem carregar(String token) throws ResourceNotFoundException {
        String sql = "SELECT v.veiculo_id, v.sensor_id, v.descricao, v.status_viagem, v.saida_em, v.saida_real_em, v.entregue_em, v.atualizado_em, v.criado_em, v.id, v.token, s.token AS sensor_token, v.motorista_id FROM viagem v LEFT JOIN sensor s ON s.id = v.sensor_id WHERE v.token = ?";

        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapRow(rs), token);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException();
        }
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
                rs.getObject("saida_real_em", OffsetDateTime.class) != null
                        ? rs.getObject("saida_real_em", OffsetDateTime.class).toLocalDateTime() : null,
                rs.getObject("entregue_em", OffsetDateTime.class) != null
                        ? rs.getObject("entregue_em", OffsetDateTime.class).toLocalDateTime() : null,
                rs.getObject("atualizado_em", OffsetDateTime.class) != null
                        ? rs.getObject("atualizado_em", OffsetDateTime.class).toLocalDateTime() : null,
                rs.getLong("motorista_id")


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
    public Page<Viagem> findByStatusAndData(String status, LocalDate dataInicio, LocalDate dataFim, int page, Boolean isMotorista, Long id) {
        StringBuilder sql = new StringBuilder("""
    SELECT v.*, s.token AS sensor_token
    FROM viagem v
    LEFT JOIN sensor s ON s.id = v.sensor_id
    WHERE 1=1
""");

        List<Object> params = new ArrayList<>();
        StringBuilder filterSql = new StringBuilder();

        if (status != null && !status.isBlank()) {
            filterSql.append(" AND v.status_viagem = ?");
            params.add(status);
        }
        if (dataInicio != null) {
            filterSql.append(" AND v.saida_real_em >= ?");
            params.add(dataInicio);
        }
        if (dataFim != null) {
            filterSql.append(" AND v.saida_real_em <= ?");
            params.add(dataFim);
        }if(isMotorista){
            filterSql.append(" AND v.motorista_id = ?");
            params.add(id);
        }

        String sqlCount = "SELECT COUNT(*) FROM viagem v WHERE 1=1" + filterSql.toString();
        Long totalRegistros = jdbcTemplate.queryForObject(sqlCount, Long.class, params.toArray());

        if (totalRegistros == null) {
            totalRegistros = 0L;
        }

        int pageSize = 10;
        int totalPaginas = (int) Math.ceil((double) totalRegistros / pageSize);

        int paginaAjustada = page - 1;
        if (paginaAjustada < 0) {
            paginaAjustada = 0;
        }

        if (paginaAjustada >= totalPaginas || totalRegistros == 0) {
            return new Page<>(new ArrayList<>(), totalPaginas);
        }

        sql.append(filterSql);
        sql.append(" LIMIT ").append(pageSize).append(" OFFSET ").append(paginaAjustada * pageSize);

        List<Viagem> viagens = jdbcTemplate.query(sql.toString(), (rs, rowNum) -> mapRow(rs), params.toArray());

        return new Page<>(viagens, totalPaginas);
    }

    @Override
    public List<Viagem> listAllPendente() {
        String sql = """
        SELECT v.id, v.veiculo_id, v.sensor_id, v.descricao, v.status_viagem, 
               v.saida_em, v.saida_real_em, v.entregue_em, v.atualizado_em, 
               v.criado_em, v.token, v.motorista_id, s.token AS sensor_token
        FROM viagem v
        LEFT JOIN sensor s ON s.id = v.sensor_id
        WHERE v.status_viagem = ?
    """;

        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> mapRow(rs), StatusViagem.PENDENTE.name());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }
}
