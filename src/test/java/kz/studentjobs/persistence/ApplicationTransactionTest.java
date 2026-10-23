package kz.studentjobs.persistence;

import kz.studentjobs.config.Application;
import kz.studentjobs.config.ApplicationService;
import kz.studentjobs.domain.ApplicationId;
import kz.studentjobs.domain.DuplicateApplication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = Application.class)
class ApplicationTransactionTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private ApplicationJdbc applications;

    @Autowired
    private ApplicationService service;

    @BeforeEach
    void resetDatabase() throws IOException {

        jdbc.execute("DROP TABLE IF EXISTS job_application");

        String schema = Files.readString(
                Path.of("src/main/resources/db/schema.sql")
        );

        jdbc.execute(schema);
    }

    @Test
    void secondStatementRollsBack() {

        assertThrows(
                DuplicateApplication.class,
                () -> service.insertTwice("APP-1042")
        );

        assertEquals(
                0,
                applications.count("APP-1042")
        );
    }

    @Test
    void secondRequestKeepsTheFirst() {

        service.register(
                ApplicationId.newId(),
                "APP-1042",
                "Warehouse application"
        );

        assertThrows(
                DuplicateApplication.class,
                () -> service.register(
                        ApplicationId.newId(),
                        "APP-1042",
                        "Again"
                )
        );

        assertEquals(
                1,
                applications.count("APP-1042")
        );
    }
}