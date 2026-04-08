package com.beeftracker.backend.veiculos.repositories;

import com.beeftracker.backend.veiculos.models.VeiculoData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class VeiculoRepositoryImpl implements VeiculoCustomRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Long salvar(VeiculoData veiculo) {
        String sql = "INSERT INTO veiculo (placa, modelo, marca, ano, capacidade_carga, ativo) " +
                "VALUES (:placa, :modelo, :marca, :ano, :capacidadeCarga, :ativo)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("placa", veiculo.placa())
                .addValue("modelo", veiculo.modelo())
                .addValue("marca", veiculo.marca())
                .addValue("ano", veiculo.ano())
                .addValue("capacidadeCarga", veiculo.capacidadeCarga())
                .addValue("ativo", veiculo.ativo() != null ? veiculo.ativo() : true);

        jdbcTemplate.update(sql, params, keyHolder, new String[] { "id" });

        return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
    }

    @Override
    public void atualizar(Long id, VeiculoData veiculo) {
        String sql = "UPDATE veiculo SET placa = :placa, modelo = :modelo, marca = :marca, " +
                "ano = :ano, capacidade_carga = :capacidadeCarga WHERE id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("placa", veiculo.placa())
                .addValue("modelo", veiculo.modelo())
                .addValue("marca", veiculo.marca())
                .addValue("ano", veiculo.ano())
                .addValue("capacidadeCarga", veiculo.capacidadeCarga())
                .addValue("id", id);

        jdbcTemplate.update(sql, params);
    }

    @Override
    public void atualizarStatus(Long id, Boolean status) {
        String sql = "UPDATE veiculo SET ativo = :status WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("status", status)
                .addValue("id", id);
        jdbcTemplate.update(sql, params);
    }
}