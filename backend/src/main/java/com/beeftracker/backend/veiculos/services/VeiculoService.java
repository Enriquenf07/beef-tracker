package com.beeftracker.backend.veiculos.services;

import com.beeftracker.backend.base.exceptions.InvalidFormException;
import com.beeftracker.backend.base.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beeftracker.backend.veiculos.models.Veiculo;
import com.beeftracker.backend.veiculos.models.VeiculoData;
import com.beeftracker.backend.veiculos.repositories.VeiculoRepository;

import java.util.List;

@Service
public class VeiculoService {

    @Autowired
    private VeiculoRepository veiculoRepository;

    public Long salvar(VeiculoData data) throws InvalidFormException {
        data.validate();
        if (veiculoRepository.buscarPorPlaca(data.placa()).isPresent()) {
            throw new InvalidFormException();
        }
        return veiculoRepository.salvar(data);
    }

    public void editar(Long id, VeiculoData data) throws InvalidFormException, ResourceNotFoundException {
        buscarPorId(id);
        data.validate();
        veiculoRepository.editar(id, data);
    }

    public void alterarStatus(Long id) throws ResourceNotFoundException {
        Veiculo veiculo = buscarPorId(id);
        veiculoRepository.alterarStatus(id, !veiculo.data().ativo());
    }

    public void excluir(Long id) throws ResourceNotFoundException {
        buscarPorId(id);
        veiculoRepository.excluir(id);
    }

    public List<Veiculo> pesquisar(String chave, Boolean status) {
        return veiculoRepository.pesquisar(chave, status);
    }

    public Veiculo buscarPorId(Long id) throws ResourceNotFoundException {
        return veiculoRepository.buscarPorId(id)
                .orElseThrow(ResourceNotFoundException::new);
    }

    public void validate(Long id) throws ResourceNotFoundException {
        buscarPorId(id);
    }
}