package com.beeftracker.backend.auth.repositories;

import com.beeftracker.backend.auth.models.user.UserData;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public class UserCustomRepositoryImpl implements UserCustomRepository{
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public UserCustomRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void salvar(UserData userData, String token, String senhaTemp) {
        String sql = "INSERT INTO usuarios(nome, email, senha, ativo, token_primeiro_acesso, token_criado_em) VALUES (:nome, :email, :senha, :ativo, :token, :tokenData)";

        jdbcTemplate.update(
                sql,
                new MapSqlParameterSource()
                        .addValue("ativo", true)
                        .addValue("nome", userData.nome())
                        .addValue("email", userData.email())
                        .addValue("senha", senhaTemp)
                        .addValue("token", token)
                        .addValue("tokenData", LocalDate.now())
        );
    }

    @Override
    public void finalizarCadastro(Long id, String senha) {
        String sql = "UPDATE usuarios SET senha = :senha, token_primeiro_acesso=:token_primeiro_acesso, token_criado_em=:token_criado_em WHERE id = :id";

        jdbcTemplate.update(
                sql,
                new MapSqlParameterSource()
                        .addValue("id", id)
                        .addValue("senha", senha)
                        .addValue("token_primeiro_acesso", null)
                        .addValue("token_criado_em", null)
        );
    }
}
