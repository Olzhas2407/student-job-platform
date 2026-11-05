package kz.studentjobs.config;

import kz.studentjobs.domain.ApplicationNotificationPort;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PortConfig {

    private final DownstreamProperties downstream;

    public PortConfig(DownstreamProperties downstream) {
        this.downstream = downstream;
    }

    @Bean
    public ApplicationNotificationPort applicationNotificationPort() {

        // Lab 4: properties are bound from yaml.
        // No real HTTP call yet.
        return businessKey -> {
            downstream.baseUrl();
            downstream.timeout();
        };
    }
}