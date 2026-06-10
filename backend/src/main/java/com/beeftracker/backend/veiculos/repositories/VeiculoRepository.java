package com.beeftracker.backend.veiculos.repositories;

import com.beeftracker.backend.veiculos.models.Veiculo;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VeiculoRepository extends ListCrudRepository<Veiculo, Long>, VeiculoCustomRepository {
}