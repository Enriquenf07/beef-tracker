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
        return MqttClient.builder()
                .useMqttVersion5()
                .serverHost(host)
                .serverPort(port)
                .sslWithDefaultConfig()
                .simpleAuth()
                .username(username)
                .password(password.getBytes())
                .applySimpleAuth()
                .buildAsync();
    }
}
