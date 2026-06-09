package com.beeftracker.backend.veiculos.services;

import com.beeftracker.backend.base.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.beeftracker.backend.veiculos.models.Veiculo;
import com.beeftracker.backend.veiculos.models.VeiculoData;
import com.beeftracker.backend.veiculos.repositories.VeiculoRepository;
import com.beeftracker.backend.base.exceptions.InvalidFormException;

import javax.annotation.Resource;
import java.util.List;

@Service
public class VeiculoService {

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Transactional
    public Veiculo salvar(VeiculoData data) throws InvalidFormException {

        data.validate();

        if (veiculoRepository.findByDataPlaca(data.placa()).isPresent()) {
            throw new InvalidFormException();
        }

        Veiculo novoVeiculo = new Veiculo(data, null);

        return veiculoRepository.save(novoVeiculo);
    }

    public List<Veiculo> listarTodos() {
        return veiculoRepository.findAll();
    }

    public void atualizar(Long id, VeiculoData data) throws ResourceNotFoundException, InvalidFormException {
        validate(id);
        data.validate();
        Veiculo existing = veiculoRepository.carregar(id);
        Veiculo updated = new Veiculo(data, existing.metadata());
        veiculoRepository.save(updated);
    }

    public void atualizarStatus(Long id) throws ResourceNotFoundException {
        Veiculo veiculo = veiculoRepository.carregar(id);
        if (veiculo == null) {
            throw new ResourceNotFoundException();
        }
        VeiculoData novoData = new VeiculoData(
                veiculo.data().placa(),
                veiculo.data().modelo(),
                veiculo.data().marca(),
                veiculo.data().ano(),
                veiculo.data().capacidadeCarga(),
                !veiculo.data().ativo());
        veiculoRepository.save(new Veiculo(novoData, veiculo.metadata()));
    }

    public void excluir(Long id) throws ResourceNotFoundException {
        validate(id);
        veiculoRepository.deleteById(id);
    }

    public void validate(Long id) throws ResourceNotFoundException {
        Veiculo veiculo = veiculoRepository.carregar(id);
        if (veiculo == null) {
            throw new ResourceNotFoundException();
        }
    }
}