package com.beeftracker.backend.viagens.model;

public record SensorLeitura(
        Number timestamp,
        double lat,
        double lon,
        double temp,
        double umidade
) {}