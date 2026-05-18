package com.beeftracker.backend.base;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.influxdb.v3.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
@Configuration
public class InfluxConfig {

    @Value("${beeftracker.influx.url}")
    private String hostUrl;

    @Value("${beeftracker.influx.token}")
    private String token;

    @Bean
    public InfluxDBClient influxDBClient() {
        return InfluxDBClient.getInstance(hostUrl, token.toCharArray(), "viagens");
    }
}
