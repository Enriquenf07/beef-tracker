package com.beeftracker.backend.veiculos.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.beeftracker.backend.veiculos.models.Veiculo;
import com.beeftracker.backend.veiculos.models.VeiculoData;
import com.beeftracker.backend.veiculos.repositories.VeiculoRepository;
import com.beeftracker.backend.base.exceptions.InvalidFormException;

import java.util.List;

@Service
public class VeiculoService {

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Transactional
    public Veiculo salvar(VeiculoData data) throws InvalidFormException {

        data.validate();

        if (veiculoRepository.findByDataPlaca(data.placa()).isPresent()) {
            throw new RuntimeException("Veículo com esta placa já cadastrado.");
        }

        Veiculo novoVeiculo = new Veiculo(null, data, null);

        return veiculoRepository.save(novoVeiculo);
    }

    public List<Veiculo> listarTodos() {
        return veiculoRepository.findAll();
    }
}