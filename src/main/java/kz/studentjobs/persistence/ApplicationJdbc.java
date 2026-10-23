package kz.studentjobs.persistence;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class ApplicationJdbc {

    private final JdbcTemplate jdbc;

    public ApplicationJdbc(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void insert(
            UUID id,
            String businessKey,
            String status,
            String title
    ) {
        jdbc.update("""
                INSERT INTO job_application
                    (id, business_key, status, title)
                VALUES (?, ?, ?, ?)
                """,
                id,
                businessKey,
                status,
                title
        );
    }

    public int count(String businessKey) {
        Integer result = jdbc.queryForObject(
                """
                SELECT count(*)
                FROM job_application
                WHERE business_key = ?
                """,
                Integer.class,
                businessKey
        );

        return result == null ? 0 : result;
    }
}