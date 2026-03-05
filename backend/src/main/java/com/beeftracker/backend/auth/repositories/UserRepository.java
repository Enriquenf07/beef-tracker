package com.beeftracker.backend.auth.repositories;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.beeftracker.backend.auth.models.user.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long>, UserCustomRepository{
    User findByDataEmail(String email);
    @Query("SELECT * FROM usuarios WHERE token_primeiro_acesso = :token")
    User findByDataTokenPrimeiroAcesso(@Param("token") String token);
}
