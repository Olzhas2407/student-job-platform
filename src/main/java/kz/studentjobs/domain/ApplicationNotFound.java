package kz.studentjobs.domain;

import java.util.UUID;

public class ApplicationNotFound extends RuntimeException {

    public ApplicationNotFound(UUID id) {
        super("Application not found: " + id);
    }
}