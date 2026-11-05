package kz.studentjobs.handler;

import java.util.UUID;

public record ApplicationResponse(
        UUID id,
        String businessKey,
        String status,
        String title
) {
}