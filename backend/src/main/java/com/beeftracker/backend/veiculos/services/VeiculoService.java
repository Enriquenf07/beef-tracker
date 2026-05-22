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

    public void validate(Long id) throws ResourceNotFoundException {
        Veiculo veiculo = veiculoRepository.carregar(id);
        if(veiculo == null){
            throw new ResourceNotFoundException();
        }
    }
}