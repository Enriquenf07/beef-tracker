package com.beeftracker.backend.compras.fornecedores.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.beeftracker.backend.compras.fornecedores.models.FornecedorData;

@Repository
public class FornecedorCustomRepositoryImpl implements FornecedorCustomRepository {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void salvar(FornecedorData fornecedor) {
        String sql = "INSERT INTO fornecedores(nome, apelido, cep, cnpj, endereco) VALUES (:nome, :apelido, :cep, :cnpj, :endereco)";
        jdbcTemplate.update(sql, new BeanPropertySqlParameterSource(fornecedor));
    }

    @Override
    public void atualizar(Long id, FornecedorData fornecedor) {
        String sql = "UPDATE fornecedores SET nome = :nome, apelido = :apelido, " +
                "cep = :cep, cnpj = :cnpj, endereco = :endereco WHERE id = :id";
        MapSqlParameterSource paramsMap = new MapSqlParameterSource()
                .addValue("nome", fornecedor.nome())
                .addValue("apelido", fornecedor.apelido())
                .addValue("cep", fornecedor.cep())
                .addValue("cnpj", fornecedor.cnpj())
                .addValue("endereco", fornecedor.endereco())
                .addValue("id", id);
        jdbcTemplate.update(sql, paramsMap);
    }

    @Override
    public void atualizarStatus(Long id, Boolean status) {
        String sql = "UPDATE fornecedores SET ativo = :status WHERE id = :id";
        MapSqlParameterSource paramsMap = new MapSqlParameterSource()
                .addValue("status", status)
                .addValue("id", id);
        jdbcTemplate.update(sql, paramsMap);
    }
}
