package com.beeftracker.backend.ativos.sensores.repositories;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.beeftracker.backend.ativos.sensores.models.Sensor;
import com.beeftracker.backend.ativos.sensores.models.SensorData;

public interface SensorRepository {
    void salvar(SensorData data);
    void editar(Long id, SensorData data);
    void editarStatus(Long id, boolean ativo);
    Sensor carregar(Long id);
    List<Sensor> pesquisar(String chave, Boolean status);
}