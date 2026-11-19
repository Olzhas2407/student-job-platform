package kz.studentjobs.handler;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}