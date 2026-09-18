package kz.studentjobs;

public record ApplicationId(String value) {

    public ApplicationId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Application id cannot be null or blank"
            );
        }
    }
}