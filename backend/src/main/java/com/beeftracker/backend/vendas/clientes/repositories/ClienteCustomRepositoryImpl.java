package com.beeftracker.backend.vendas.clientes.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.beeftracker.backend.vendas.clientes.models.ClienteData;

@Repository
public class ClienteCustomRepositoryImpl implements ClienteCustomRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Long salvar(ClienteData cliente) {
        String sql = "INSERT INTO clientes(nome, apelido, cpf_cnpj, email, telefone, cep, uf) " +
                "VALUES (:nome, :apelido, :cpfCnpj, :email, :telefone, :cep, :uf)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(sql, toParams(cliente), keyHolder, new String[] { "id" });
        return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
    }

    @Override
    public void atualizar(Long id, ClienteData cliente) {
        String sql = "UPDATE clientes SET nome = :nome, apelido = :apelido, cpf_cnpj = :cpfCnpj, " +
                "email = :email, telefone = :telefone, cep = :cep, uf = :uf WHERE id = :id";
        jdbcTemplate.update(sql, toParams(cliente).addValue("id", id));
    }

    @Override
    public void atualizarStatus(Long id, Boolean status) {
        String sql = "UPDATE clientes SET ativo = :status WHERE id = :id";
        jdbcTemplate.update(sql, new MapSqlParameterSource()
                .addValue("status", status)
                .addValue("id", id));
    }

    private MapSqlParameterSource toParams(ClienteData c) {
        return new MapSqlParameterSource()
                .addValue("nome", c.nome())
                .addValue("apelido", c.apelido())
                .addValue("cpfCnpj", c.cpfCnpj())
                .addValue("email", c.email())
                .addValue("telefone", c.telefone())
                .addValue("cep", c.cep())
                .addValue("uf", c.uf());
    }
}