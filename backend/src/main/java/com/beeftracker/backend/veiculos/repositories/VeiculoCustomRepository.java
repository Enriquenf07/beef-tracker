package com.beeftracker.backend.veiculos.repositories;

import com.beeftracker.backend.veiculos.models.Veiculo;
import com.beeftracker.backend.veiculos.models.VeiculoData;

import java.util.List;
import java.util.Optional;

public interface VeiculoCustomRepository {
    Long salvar(VeiculoData data);

    void editar(Long id, VeiculoData data);

    void alterarStatus(Long id, Boolean ativo);

    void excluir(Long id);

    Optional<Veiculo> buscarPorId(Long id);

    Optional<Veiculo> buscarPorPlaca(String placa);

    List<Veiculo> pesquisar(String chave, Boolean status);
}