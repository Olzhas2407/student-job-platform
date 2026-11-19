package kz.studentjobs.security;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import kz.studentjobs.handler.ApiError;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.util.UUID;

public class JwtAuthenticationEntryPoint
        implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public JwtAuthenticationEntryPoint(
            ObjectMapper objectMapper
    ) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        String correlationId =
                correlationId(request);

        ApiError error =
                new ApiError(
                        "UNAUTHENTICATED",
                        "valid token required",
                        correlationId
                );

        response.setStatus(
                HttpServletResponse.SC_UNAUTHORIZED
        );

        response.setContentType(
                MediaType.APPLICATION_JSON_VALUE
        );

        objectMapper.writeValue(
                response.getOutputStream(),
                error
        );
    }

    private String correlationId(
            HttpServletRequest request
    ) {

        String header =
                request.getHeader("X-Correlation-Id");

        if (header == null || header.isBlank()) {
            return UUID.randomUUID().toString();
        }

        return header;
    }
}