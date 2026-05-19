package com.beeftracker.backend.viagens.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

import com.beeftracker.backend.viagens.model.*;
import com.influxdb.v3.client.InfluxDBClient;
import com.influxdb.v3.client.PointValues;
import com.influxdb.v3.client.query.QueryOptions;
import com.influxdb.v3.client.query.QueryType;
import org.springframework.stereotype.Service;

import com.beeftracker.backend.base.exceptions.ResourceNotFoundException;
import com.beeftracker.backend.viagens.repository.ViagemRepository;
import com.beeftracker.backend.viagens.strategy.AlterarStatus;
import com.beeftracker.backend.viagens.strategy.Cancelada;
import com.beeftracker.backend.viagens.strategy.EmTransito;
import com.beeftracker.backend.viagens.strategy.Entregue;
@Service
public class ViagemService {
    private final ViagemRepository repository;
    private final HashMap<String, AlterarStatus> services;
    private final InfluxDBClient influxDBClient;

    public ViagemService(
            ViagemRepository repository,
            EmTransito transito,
            Entregue concluida,
            Cancelada cancelada, InfluxDBClient influxDBClient) {
        this.repository = repository;
        this.influxDBClient = influxDBClient;
        services = new HashMap<>();
        services.put("EM_TRANSITO", transito);
        services.put("CONCLUIDA", concluida);
        services.put("CANCELADA", cancelada);
    }

    public void editar(Long id, String descricao) throws ResourceNotFoundException {
        Viagem viagem = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException());
        viagem = new Viagem(new ViagemData(
                viagem.data().veiculoId(),
                viagem.data().sensorId(),
                "",
                descricao,
                viagem.data().statusViagem(),
                viagem.data().saidaRealEm(),
                viagem.data().saidaEm(),
                viagem.data().entregueEm(),
                viagem.data().atualizadoEm()), viagem.metadata());
        repository.editar(viagem.data(), id);
    }

    public void alterarStatus(Long id, NovoStatus status) throws ResourceNotFoundException {
        Viagem viagem = repository.carregar(id);
        AlterarStatus service = services.get(status.novoStatus().toString());
        viagem = service.alterarStatus(viagem, StatusViagem.valueOf(status.novoStatus()));
        repository.editar(viagem.data(), id);
    }

    public List<Viagem> pesquisar(String status, int page) {
        return repository.findByStatus(status, page);
    }

    public void criar(ViagemData data) {
        ViagemData novaViagem = new ViagemData(
                data.veiculoId(),
                data.sensorId(),
                "",
                data.descricao(),
                StatusViagem.PENDENTE,
                null,
                data.saidaEm(),
                data.entregueEm(),
                data.atualizadoEm());

        repository.criar(novaViagem);
    }

    public StatsViagem getStats(Long viagemId) {
        Viagem viagem = repository.carregar(viagemId);
        List<SensorLeitura> leituras = getLeituras(viagemId).get("leituras");

        if (leituras == null || leituras.isEmpty()) {
            return new StatsViagem(0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0L);
        }

        DoubleSummaryStatistics statsTemp = leituras.stream()
                .mapToDouble(SensorLeitura::temp)
                .summaryStatistics();

        DoubleSummaryStatistics statsUmidade = leituras.stream()
                .mapToDouble(SensorLeitura::umidade)
                .summaryStatistics();

        long timestampInicialRaw = leituras.get(0).timestamp().longValue();
        long timestampFinalRaw = leituras.get(leituras.size() - 1).timestamp().longValue();

        long diferencaNanosegundos = timestampFinalRaw - timestampInicialRaw;

        long duracaoSegundos = diferencaNanosegundos / 1_000_000_000L;



        return new StatsViagem(
                leituras.size(),
                statsTemp.getAverage(),
                statsTemp.getMin(),
                statsTemp.getMax(),
                statsUmidade.getAverage(),
                statsUmidade.getMin(),
                statsUmidade.getMax(),
                duracaoSegundos
        );
    }

    public HashMap<String, List<SensorLeitura>> getLeituras(Long viagemId) {
        Viagem viagem = repository.carregar(viagemId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if(viagem.data().saidaRealEm() == null || viagem.data().statusViagem() == StatusViagem.PENDENTE || viagem.data().statusViagem() == StatusViagem.CANCELADA){
            return null; // TODO add erro depois
        }

        String saidaEm = viagem.data().saidaRealEm()
                .atZone(ZoneId.of("America/Sao_Paulo"))
                .toInstant()
                .toString();

        String entregueEm = viagem.data().entregueEm() != null
                ? viagem.data().entregueEm()
                .atZone(ZoneId.of("America/Sao_Paulo"))
                .toInstant()
                .toString()
                : Instant.now().toString();

        String sql = "SELECT * " +
                "FROM 'viagem' " +
                "WHERE sensor_uuid = $sensorUuid " +
                "AND time >= $saidaEm " +
                "AND time <= $entregueEm " +
                "ORDER BY time ASC";

        List<SensorLeitura> leituras = new ArrayList<>();

        try (Stream<PointValues> points = influxDBClient.queryPoints(sql,
                Map.of(
                        "sensorUuid", viagem.data().sensorToken(),
                        "saidaEm", saidaEm,
                        "entregueEm", entregueEm
                ),
                new QueryOptions(QueryType.SQL))) {

            points.forEach(pv -> {
                SensorLeitura leitura = new SensorLeitura(
                        pv.getTimestamp(),
                        pv.getField("lat", Double.class),
                        pv.getField("lon", Double.class),
                        pv.getField("temp", Double.class),
                        pv.getField("umidade", Double.class)
                );
                leituras.add(leitura);
            });
        }
        HashMap<String, List<SensorLeitura>> map = new HashMap<>();
        map.put("leituras", leituras);
        return map;
    }

}