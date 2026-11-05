package kz.studentjobs.handler;

public record ApiError(
        String code,
        String message,
        String correlationId
) {
}