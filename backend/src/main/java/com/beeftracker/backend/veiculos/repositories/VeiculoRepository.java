package com.beeftracker.backend.veiculos.repositories;

import java.util.List;
import java.util.Optional;

import com.beeftracker.backend.viagens.model.Viagem;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import com.beeftracker.backend.veiculos.models.Veiculo;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

@Repository
public interface VeiculoRepository extends ListCrudRepository<Veiculo, Long> {
    Optional<Veiculo> findByDataPlaca(String placa);
    @Query("""
            SELECT * FROM veiculo
            WHERE id = :id
            """)
    Veiculo carregar(@Param("id") long id);
}