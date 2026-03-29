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
import java.util.List;

@Repository
public class UserCustomRepositoryImpl implements UserCustomRepository{
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

    @Override
    public RolesFull findRolesByUser(Long userId) {
        String sql = "SELECT r.nome, r.id from roles r LEFT JOIN role_usuario ru ON ru.role_id = r.id WHERE ru.usuario_id = :id";
        List<Role> roles = jdbcTemplate.query(
                sql,
                new MapSqlParameterSource()
                        .addValue("id", userId),
                 (rs, rowNum) -> new Role(rs.getString("nome"), Long.parseLong(rs.getString("id")))
        );
        return new RolesFull(roles);
    }

    @Override
    public List<User> pesquisar(String chave, Boolean status) {
        QueryBuilder qb = new QueryBuilder()
                .select("SELECT u.id, u.nome, u.email, u.ativo, u.token,\n" +
                        "ARRAY_REMOVE(ARRAY_AGG(r.nome), NULL) as roles\n" +
                        "FROM usuarios u\n" +
                        "LEFT JOIN role_usuario ru ON u.id = ru.usuario_id\n" +
                        "LEFT JOIN roles r ON ru.role_id = r.id")
                .orderBy("u.id ASC")
                .limit(2)
                .groupBy("GROUP BY u.id")
                .offset(0);
        if(StringUtils.isNotBlank(chave)){
            chave = chave.toLowerCase();
            qb.where("WHERE u.nome like %:chave%");
        }
        if(status != null){
            qb.where("AND status = :status");
        }
        String query = qb.build();


        List<User> usuarios = jdbcTemplate.query(
                query,
                new MapSqlParameterSource()
                        .addValue("chave", chave)
                        .addValue("status", status),
                (rs, rowNum) -> factory.create(rs)
        );
        return usuarios;
    }

    @Override
    public RolesFull findAllRoles() {
        String sql = "SELECT r.nome, r.id from roles r";
        List<Role> roles = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new Role(rs.getString("nome"), Long.parseLong(rs.getString("id")))
        );
        return new RolesFull(roles);
    }
}
