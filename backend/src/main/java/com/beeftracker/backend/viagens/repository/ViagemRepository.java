package com.beeftracker.backend.viagens.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.beeftracker.backend.viagens.model.Viagem;

public interface ViagemRepository extends CrudRepository<Viagem, Long>, ViagemCustomRepository {

    @Query("""
            SELECT * FROM viagem
            WHERE status_viagem = :status
            LIMIT 10 OFFSET (:page * 10)
            """)
    List<Viagem> findByStatus(@Param("status") String status, @Param("page") int page);


    @Query("""
        SELECT v.veiculo_id, v.sensor_id, v.descricao, v.status_viagem,
               v.saida_em, v.saida_real_em, v.entregue_em, v.atualizado_em,
               v.criado_em, v.id, v.token, s.token AS sensor_token
        FROM viagem v
        LEFT JOIN sensor s ON s.id = v.sensor_id
        WHERE s.token = :sensorToken::uuid
        AND v.status_viagem = 'EM_TRANSITO'
        LIMIT 1
        """)
    Viagem findBySensorTokenAtiva(@Param("sensorToken") String sensorToken);

    @Query("""
        SELECT v.veiculo_id, v.sensor_id, v.descricao, v.status_viagem,
               v.saida_em, v.saida_real_em, v.entregue_em, v.atualizado_em,
               v.criado_em, v.id, v.token, s.token AS sensor_token
        FROM viagem v
        LEFT JOIN sensor s ON s.id = v.sensor_id
        WHERE v.sensor_id = :sensorId
        AND v.status_viagem = 'EM_TRANSITO'
        LIMIT 1
        """)
    Viagem findBySensorIdAtiva(@Param("sensorId") Long sensorId);
}
