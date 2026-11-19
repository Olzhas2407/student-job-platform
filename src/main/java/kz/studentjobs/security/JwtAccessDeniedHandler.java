package kz.studentjobs.security;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import kz.studentjobs.handler.ApiError;

import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.util.UUID;

public class JwtAccessDeniedHandler
        implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public JwtAccessDeniedHandler(
            ObjectMapper objectMapper
    ) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {

        String correlationId =
                correlationId(request);

        ApiError error =
                new ApiError(
                        "FORBIDDEN",
                        "not allowed for your role",
                        correlationId
                );

        response.setStatus(
                HttpServletResponse.SC_FORBIDDEN
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