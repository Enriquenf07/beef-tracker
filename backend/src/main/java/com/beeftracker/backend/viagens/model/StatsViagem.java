package com.beeftracker.backend.viagens.model;

public record StatsViagem(
        int totalLeituras,
        double tempMedia,
        double tempMin,
        double tempMax,
        double umidadeMedia,
        double umidadeMin,
        double umidadeMax,
        long duracaoSegundos
) {}
