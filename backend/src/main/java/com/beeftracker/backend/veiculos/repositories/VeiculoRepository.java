package com.beeftracker.backend.veiculos.repositories;

import com.beeftracker.backend.veiculos.models.Veiculo;
import org.springframework.data.jdbc.repository.query.Query; // Faltava este
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param; // Faltava este
import org.springframework.stereotype.Repository;

import java.util.List; // Faltava este
import java.util.Optional;

@Repository
public interface VeiculoRepository extends ListCrudRepository<Veiculo, Long>, VeiculoCustomRepository {

    @Query("SELECT * FROM veiculo v WHERE " +
            "(:chave IS NULL OR LOWER(v.placa) LIKE LOWER(CONCAT('%', :chave, '%')) OR " +
            "LOWER(v.modelo) LIKE LOWER(CONCAT('%', :chave, '%'))) AND " +
            "(:status IS NULL OR v.ativo = :status)")
    List<Veiculo> pesquisar(@Param("chave") String chave, @Param("status") Boolean status);

    @Query("SELECT * FROM veiculo WHERE placa = :placa")
    Optional<Veiculo> findByPlaca(@Param("placa") String placa);
}