package com.beeftracker.backend.vendas.clientes.repositories;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.beeftracker.backend.vendas.clientes.models.Cliente;

@Repository
public interface ClienteRepository extends CrudRepository<Cliente, Long>, ClienteCustomRepository {

    @Query("SELECT * FROM clientes WHERE id = :id")
    Optional<Cliente> buscarPorId(Long id);
}