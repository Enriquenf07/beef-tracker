package com.beeftracker.backend.compras.fornecedores.services;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.beeftracker.backend.compras.fornecedores.models.Fornecedor;
import com.beeftracker.backend.compras.fornecedores.models.FornecedorData;
import com.beeftracker.backend.compras.fornecedores.repositories.FornecedorRepository;

@Service
public class FornecedorService {
    public final FornecedorRepository fornecedorRepository;

    public FornecedorService(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = fornecedorRepository;
    }

    public List<Fornecedor> pesquisar(String chave, Boolean status) {
        List<Fornecedor> fornecedores = (List<Fornecedor>) fornecedorRepository.pesquisar(chave, status);
        return fornecedores.stream().sorted(Comparator.comparingLong(f -> f.metadata().id())).toList();
    }

    public void cadastrar(FornecedorData fornecedorData) {
        fornecedorRepository.salvar(fornecedorData);
    }

    public void atualizar(Long id, FornecedorData fornecedorData) {
        fornecedorRepository.atualizar(id, fornecedorData);
    }

    public void atualizarStatus(Long id, Boolean status) {
        fornecedorRepository.atualizarStatus(id, status);
    }

}
