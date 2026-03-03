package com.beeftracker.backend.compras.fornecedores.services;

import java.util.Comparator;
import java.util.List;

import com.beeftracker.backend.base.exceptions.InvalidFormException;
import com.beeftracker.backend.base.exceptions.ResourceNotFoundException;
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

    public Long cadastrar(FornecedorData fornecedorData) throws InvalidFormException {
        fornecedorData.validate();
        return fornecedorRepository.salvar(fornecedorData);
    }

    public void atualizar(Long id, FornecedorData fornecedorData) throws ResourceNotFoundException, InvalidFormException {
        findById(id);
        fornecedorData.validate();
        fornecedorRepository.atualizar(id, fornecedorData);
    }

    public void atualizarStatus(Long id) throws ResourceNotFoundException {
        Fornecedor fornecedor = findById(id);
        fornecedorRepository.atualizarStatus(id, !fornecedor.data().ativo());
    }

    public Fornecedor findById(Long id) throws ResourceNotFoundException {
        return fornecedorRepository.buscarPorId(id).orElseThrow(() -> new ResourceNotFoundException());
    }
}
