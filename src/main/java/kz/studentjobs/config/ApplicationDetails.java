package kz.studentjobs.config;

import java.util.UUID;

public record ApplicationDetails(
        UUID id,
        String businessKey,
        String status,
        String title
) {
}