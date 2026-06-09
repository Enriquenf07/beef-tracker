package com.beeftracker.backend.vendas.clientes.repositories;

import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.beeftracker.backend.auth.models.metadata.Metadata;
import com.beeftracker.backend.vendas.clientes.models.Cliente;
import com.beeftracker.backend.vendas.clientes.models.ClienteData;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public List<Cliente> pesquisar(String chave, Boolean status) {
        StringBuilder sql = new StringBuilder(
                "SELECT id, nome, apelido, cpf_cnpj, email, telefone, cep, uf, ativo, criado_em, atualizado_em FROM clientes ");

        List<String> conditions = new ArrayList<>();

        if (StringUtils.isNotBlank(chave)) {
            conditions.add(
                    "(LOWER(nome) LIKE :chave OR LOWER(cpf_cnpj) LIKE :chave OR LOWER(email) LIKE :chave OR LOWER(apelido) LIKE :chave)");
        }

        if (status != null) {
            conditions.add("ativo = :status");
        }

        if (!conditions.isEmpty()) {
            sql.append("WHERE ").append(String.join(" AND ", conditions)).append(" ");
        }

        sql.append("ORDER BY id ASC");

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (StringUtils.isNotBlank(chave)) {
            params.addValue("chave", "%" + chave.toLowerCase() + "%");
        }
        if (status != null) {
            params.addValue("status", status);
        }

        return jdbcTemplate.query(sql.toString(), params, (rs, rowNum) -> {
            OffsetDateTime criadoOdt = rs.getObject("criado_em", OffsetDateTime.class);
            OffsetDateTime atualizadoOdt = rs.getObject("atualizado_em", OffsetDateTime.class);
            LocalDate criadoDate = (criadoOdt != null) ? criadoOdt.toLocalDate() : null;
            LocalDate atualizadoDate = (atualizadoOdt != null) ? atualizadoOdt.toLocalDate() : null;

            return new Cliente(
                    new ClienteData(
                            rs.getString("nome"),
                            rs.getString("apelido"),
                            rs.getString("cpf_cnpj"),
                            rs.getString("email"),
                            rs.getString("telefone"),
                            rs.getString("cep"),
                            rs.getString("uf"),
                            rs.getBoolean("ativo")),
                    new Metadata(criadoDate, atualizadoDate, rs.getLong("id"), null));
        });
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