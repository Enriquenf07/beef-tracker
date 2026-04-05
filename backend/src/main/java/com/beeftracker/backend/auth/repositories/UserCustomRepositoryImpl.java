package com.beeftracker.backend.auth.repositories;

import com.beeftracker.backend.auth.models.factory.UserFactory;
import com.beeftracker.backend.auth.models.user.User;
import com.beeftracker.backend.auth.models.user.UserData;
import com.beeftracker.backend.base.query.QueryBuilder;
import com.beeftracker.backend.usuarios.models.Role;
import com.beeftracker.backend.usuarios.models.Roles;
import com.beeftracker.backend.usuarios.models.RolesFull;

import io.micrometer.common.util.StringUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserCustomRepositoryImpl implements UserCustomRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final UserFactory factory;

    public UserCustomRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate, UserFactory factory) {
        this.jdbcTemplate = jdbcTemplate;
        this.factory = factory;
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
                        .addValue("tokenData", LocalDate.now()));
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
                        .addValue("token_criado_em", null));
    }

    @Override
    public RolesFull findRolesByUser(Long userId) {
        String sql = "SELECT r.nome, r.id from roles r LEFT JOIN role_usuario ru ON ru.role_id = r.id WHERE ru.usuario_id = :id";
        List<Role> roles = jdbcTemplate.query(
                sql,
                new MapSqlParameterSource()
                        .addValue("id", userId),
                (rs, rowNum) -> new Role(rs.getString("nome"), Long.parseLong(rs.getString("id"))));
        return new RolesFull(roles);
    }

    @Override
    public List<User> pesquisar(String chave, Boolean status) {
        StringBuilder sql = new StringBuilder(
                "SELECT u.id, u.nome, u.email, u.ativo, u.token, " +
                        "ARRAY_REMOVE(ARRAY_AGG(r.nome), NULL) as roles, " +
                        "CASE WHEN u.token_primeiro_acesso IS NOT NULL THEN false ELSE true END as cadastrado " +
                        "FROM usuarios u " +
                        "LEFT JOIN role_usuario ru ON u.id = ru.usuario_id " +
                        "LEFT JOIN roles r ON ru.role_id = r.id ");

        List<String> conditions = new ArrayList<>();

        if (StringUtils.isNotBlank(chave)) {
            conditions.add("LOWER(u.nome) LIKE :chave");
        }

        if (status != null) {
            conditions.add("u.ativo = :status");
        }

        if (!conditions.isEmpty()) {
            sql.append("WHERE ").append(String.join(" AND ", conditions)).append(" ");
        }

        sql.append("GROUP BY u.id, u.nome, u.email, u.ativo, u.token ");
        sql.append("ORDER BY u.id ASC ");
        sql.append("LIMIT :limit OFFSET :offset");

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("chave", StringUtils.isNotBlank(chave) ? "%" + chave.toLowerCase() + "%" : null)
                .addValue("status", status)
                .addValue("limit", 10)
                .addValue("offset", 0);

        return jdbcTemplate.query(sql.toString(), params, (rs, rowNum) -> factory.create(rs));
    }

    @Override
    public RolesFull findAllRoles() {
        String sql = "SELECT r.nome, r.id from roles r";
        List<Role> roles = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new Role(rs.getString("nome"), Long.parseLong(rs.getString("id"))));
        return new RolesFull(roles);
    }

    @Override
    public void editarStatus(Long id, boolean ativo) {
        String sql = "UPDATE usuarios SET ativo = :ativo WHERE id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("ativo", ativo)
                .addValue("id", id);

        jdbcTemplate.update(sql, params);
    }

    @Override
    public User carregar(Long id) {
        String sql = "SELECT u.id, u.nome, u.email, u.ativo, u.token, " +
                "ARRAY_REMOVE(ARRAY_AGG(r.nome), NULL) as roles, " +
                "CASE WHEN u.token_primeiro_acesso IS NOT NULL THEN true ELSE false END as cadastrado " +
                "FROM usuarios u " +
                "LEFT JOIN role_usuario ru ON u.id = ru.usuario_id " +
                "LEFT JOIN roles r ON ru.role_id = r.id " +
                "WHERE u.id = :id " +
                "GROUP BY u.id, u.nome, u.email, u.ativo, u.token, u.token_primeiro_acesso";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);

        return jdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> factory.create(rs));
    }
}
