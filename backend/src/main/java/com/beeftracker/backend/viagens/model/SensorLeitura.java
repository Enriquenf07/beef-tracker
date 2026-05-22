package com.beeftracker.backend.viagens.model;

public record SensorLeitura(
        String sensorToken,
        Number timestamp,
        double lat,
        double lon,
        double temp,
        double umidade
) {}