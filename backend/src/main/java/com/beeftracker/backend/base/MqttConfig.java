package com.beeftracker.backend.base;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfig {

    @Value("${hivemq.host}")
    private String host;

    @Value("${hivemq.port}")
    private int port;

    @Value("${hivemq.username}")
    private String username;

    @Value("${hivemq.password}")
    private String password;

    @Bean
    public Mqtt5AsyncClient mqttClient() {
        Mqtt5AsyncClient client = MqttClient.builder()
                .useMqttVersion5()
                .serverHost(host)
                .serverPort(port)
                .sslWithDefaultConfig()
                .simpleAuth()
                .username(username)
                .password(password.getBytes())
                .applySimpleAuth()
                .buildAsync();

        // ✅ Conecta de forma bloqueante antes de retornar o bean
        // assim os subscribers já encontram o client conectado
        try {
            client.connectWith()
                    .cleanStart(true)
                    .send()
                    .get(); // bloqueia até conectar
            System.out.println("MQTT conectado ao broker: " + host);
        } catch (Exception e) {
            System.err.println("MQTT falhou ao conectar: " + e.getMessage());
        }

        return client;
    }
}