package kz.studentjobs.config;

import kz.studentjobs.domain.ApplicationNotificationPort;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PortConfig {

    @Bean
    public ApplicationNotificationPort applicationNotificationPort() {
        return businessKey -> {
            // No real HTTP call in Lab 3.
        };
    }
}