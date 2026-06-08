package com.beeftracker.backend.base;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.influxdb.v3.client.InfluxDBClient;

@Configuration
public class InfluxConfig {

    @Value("${beeftracker.influx.url}")
    private String hostUrl;

    @Value("${beeftracker.influx.token}")
    private String token;

    @Bean(destroyMethod = "close")
    public InfluxDBClient influxDBClient() {
        return InfluxDBClient.getInstance(hostUrl, token.toCharArray(), "viagens");
    }
}