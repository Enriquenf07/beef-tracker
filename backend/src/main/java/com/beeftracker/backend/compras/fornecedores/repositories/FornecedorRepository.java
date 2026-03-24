package com.beeftracker.backend.compras.fornecedores.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.beeftracker.backend.compras.fornecedores.models.Fornecedor;

@Repository
public interface FornecedorRepository extends CrudRepository<Fornecedor, Long>, FornecedorCustomRepository {
    @Query("SELECT * FROM fornecedores f WHERE " +
           "((:chave IS NULL OR LOWER(f.nome) LIKE LOWER(CONCAT('%', :chave, '%'))) OR " +
           "(:chave IS NULL OR LOWER(f.cnpj) LIKE LOWER(CONCAT('%', :chave, '%'))) OR " +
           "(:chave IS NULL OR LOWER(f.cep) LIKE LOWER(CONCAT('%', :chave, '%'))) OR " +
           "(:chave IS NULL OR LOWER(f.apelido) LIKE LOWER(CONCAT('%', :chave, '%'))) OR " +
           "(:chave IS NULL OR LOWER(f.endereco) LIKE LOWER(CONCAT('%', :chave, '%')))) AND " +
           "(:status IS NULL OR f.ativo = :status)")
    List<Fornecedor> pesquisar(@Param("chave") String chave, @Param("status") Boolean status);

    @Query("SELECT * FROM fornecedores WHERE id = :id")
    Optional<Fornecedor> buscarPorId(Long id);

}
