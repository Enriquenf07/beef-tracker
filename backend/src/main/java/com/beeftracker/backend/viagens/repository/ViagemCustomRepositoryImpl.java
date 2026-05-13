package com.beeftracker.backend.viagens.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.beeftracker.backend.auth.models.metadata.Metadata;
import com.beeftracker.backend.viagens.model.Viagem;
import com.beeftracker.backend.viagens.model.ViagemData;

@Repository
public class ViagemCustomRepositoryImpl implements ViagemCustomRepository {

    private final JdbcTemplate jdbcTemplate;

    public ViagemCustomRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void criar(ViagemData viagem) {
        String sql = """
                    INSERT INTO viagens (
                        veiculo_id,
                        sensor_id,
                        descricao,
                        status_viagem,
                        saida_em
                    ) VALUES (?, ?, ?, ?, ?)
                """;

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
                    UPDATE viagens SET
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
}
