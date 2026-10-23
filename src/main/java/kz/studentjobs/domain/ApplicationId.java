package kz.studentjobs.domain;

import java.util.UUID;

public record ApplicationId(String value) {

    public ApplicationId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Application id cannot be null or blank"
            );
        }

        try {
            UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Application id must be a valid UUID",
                    ex
            );
        }
    }

    public static ApplicationId newId() {
        return new ApplicationId(
                UUID.randomUUID().toString()
        );
    }

    public UUID asUuid() {
        return UUID.fromString(value);
    }
}