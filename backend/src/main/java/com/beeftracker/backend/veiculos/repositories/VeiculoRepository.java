package com.beeftracker.backend.veiculos.repositories;

import java.util.Optional;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import com.beeftracker.backend.veiculos.models.Veiculo;

@Repository
public interface VeiculoRepository extends ListCrudRepository<Veiculo, Long> {
    Optional<Veiculo> findByDataPlaca(String placa);
}