package com.beeftracker.backend.ativos.sensores.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.beeftracker.backend.ativos.sensores.models.Sensor;
import com.beeftracker.backend.ativos.sensores.models.SensorData;
import com.beeftracker.backend.ativos.sensores.repositories.SensorRepository;
import com.beeftracker.backend.base.exceptions.ResourceNotFoundException;

@Service
public class SensorService {
    private final SensorRepository repository;

    public SensorService(SensorRepository repository) {
        this.repository = repository;
    }

    public List<Sensor> pesquisar(String chave, Boolean status) {
        return repository.pesquisar(chave, status);
    }

    public void cadastrar(SensorData data) {
        repository.salvar(data);
    }

    public void editar(Long id, SensorData data) throws ResourceNotFoundException {
        Sensor sensor = repository.carregar(id);
        if (sensor == null) {
            throw new ResourceNotFoundException();
        }
        repository.editar(id, data);
    }

    public void editarStatus(Long id) throws ResourceNotFoundException {
        Sensor sensor = repository.carregar(id);
        if (sensor == null) {
            throw new ResourceNotFoundException();
        }
        repository.editarStatus(id, !sensor.data().ativo());
    }

    public Sensor carregar(Long id) throws ResourceNotFoundException {
        Sensor sensor = repository.carregar(id);
        if (sensor == null) {
            throw new ResourceNotFoundException();
        }
        return sensor;
    }
}
