package com.beeftracker.backend.vendas.clientes.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.beeftracker.backend.vendas.clientes.models.Cliente;

@Repository
public interface ClienteRepository extends CrudRepository<Cliente, Long>, ClienteCustomRepository {

    @Query("SELECT * FROM clientes c WHERE " +
            "((:chave IS NULL OR LOWER(c.nome) LIKE LOWER(CONCAT('%', :chave, '%'))) OR " +
            "(:chave IS NULL OR LOWER(c.cpf_cnpj) LIKE LOWER(CONCAT('%', :chave, '%'))) OR " +
            "(:chave IS NULL OR LOWER(c.email) LIKE LOWER(CONCAT('%', :chave, '%'))) OR " +
            "(:chave IS NULL OR LOWER(c.apelido) LIKE LOWER(CONCAT('%', :chave, '%')))) AND " +
            "(:status IS NULL OR c.ativo = :status)")
    List<Cliente> pesquisar(@Param("chave") String chave, @Param("status") Boolean status);

    @Query("SELECT * FROM clientes WHERE id = :id")
    Optional<Cliente> buscarPorId(Long id);
}