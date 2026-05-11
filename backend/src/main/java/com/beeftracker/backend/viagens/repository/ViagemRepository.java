package com.beeftracker.backend.viagens.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.beeftracker.backend.viagens.model.Viagem;

public interface ViagemRepository extends CrudRepository<Viagem, Long> {
 
    @Query("""
            SELECT * FROM viagem
            WHERE status_viagem = :status
            LIMIT 10 OFFSET (:page * 10)
            """)
    List<Viagem> findByStatus(@Param("status") String status, @Param("page") int page);
}
