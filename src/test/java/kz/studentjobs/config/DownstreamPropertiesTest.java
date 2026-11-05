package kz.studentjobs.config;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("dev")
class DownstreamPropertiesTest {

    @Autowired
    private DownstreamProperties properties;

    @Test
    void devProfileLoadsDownstreamProperties() {

        assertEquals(
                URI.create("http://localhost:9090"),
                properties.baseUrl()
        );

        assertEquals(
                Duration.ofSeconds(2),
                properties.timeout()
        );
    }
}