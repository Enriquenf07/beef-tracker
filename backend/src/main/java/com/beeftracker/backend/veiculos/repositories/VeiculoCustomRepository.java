package com.beeftracker.backend.veiculos.repositories;

import com.beeftracker.backend.veiculos.models.VeiculoData;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface VeiculoCustomRepository {
    Long salvar(VeiculoData veiculo);

    void atualizar(Long id, VeiculoData veiculoData);

    void atualizarStatus(Long id, Boolean status);
}