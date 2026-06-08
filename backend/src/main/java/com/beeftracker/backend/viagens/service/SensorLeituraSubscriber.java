package com.beeftracker.backend.viagens.service;

import com.beeftracker.backend.viagens.model.SensorLeitura;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class SensorLeituraSubscriber {

    private final Mqtt5AsyncClient mqttClient;
    private final ObjectMapper objectMapper;
    private final ViagemService viagemService;

    private static final String TOPICO = "sensor/leitura";

    @PostConstruct
    public void iniciar() {
        subscribe();
    }

    private void subscribe() {
        mqttClient.subscribeWith()
                .topicFilter(TOPICO)
                .qos(MqttQos.AT_LEAST_ONCE)
                .callback(this::processarMensagem)
                .send()
                .whenComplete((ack, throwable) -> {
                    if (throwable != null) {
                        System.err.println("MQTT subscribe falhou [" + TOPICO + "]: " + throwable.getMessage());
                    } else {
                        System.out.println("MQTT subscrito no tópico: " + TOPICO);
                    }
                });
    }

    private void processarMensagem(Mqtt5Publish publish) {
        try {
            String payload = new String(publish.getPayloadAsBytes());
            System.out.println("MQTT recebido [" + TOPICO + "]: " + payload);
            SensorLeitura request = objectMapper.readValue(payload, SensorLeitura.class);
            viagemService.criarLeitura(request);

        } catch (IllegalStateException e) {
            System.out.println("Leitura descartada: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}