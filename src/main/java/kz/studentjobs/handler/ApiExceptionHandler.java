package kz.studentjobs.handler;

import jakarta.servlet.http.HttpServletRequest;

import kz.studentjobs.domain.ApplicationNotFound;
import kz.studentjobs.domain.DuplicateApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.UUID;

@RestControllerAdvice(assignableTypes = ApplicationController.class)
public class ApiExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error ->
                        error.getField()
                                + " "
                                + error.getDefaultMessage()
                )
                .orElse("validation failed");

        return error(
                HttpStatus.BAD_REQUEST,
                "VALIDATION",
                message,
                request
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> invalidPathId(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request
    ) {

        return error(
                HttpStatus.BAD_REQUEST,
                "VALIDATION",
                "id must be a UUID",
                request
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> invalidJson(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {

        return error(
                HttpStatus.BAD_REQUEST,
                "VALIDATION",
                "body must be JSON",
                request
        );
    }

    @ExceptionHandler(ApplicationNotFound.class)
    public ResponseEntity<ApiError> notFound(
            ApplicationNotFound ex,
            HttpServletRequest request
    ) {

        return error(
                HttpStatus.NOT_FOUND,
                "NOT_FOUND",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(DuplicateApplication.class)
    public ResponseEntity<ApiError> duplicate(
            DuplicateApplication ex,
            HttpServletRequest request
    ) {

        return error(
                HttpStatus.CONFLICT,
                "DUPLICATE",
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiError> unsupported(
            HttpMediaTypeNotSupportedException ex,
            HttpServletRequest request
    ) {

        return error(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "UNSUPPORTED",
                "unsupported media type",
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> internal(
            Exception ex,
            HttpServletRequest request
    ) {

        String correlationId = correlationId(request);

        log.error(
                "Unexpected error, correlationId={}",
                correlationId,
                ex
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        new ApiError(
                                "INTERNAL",
                                "unexpected error",
                                correlationId
                        )
                );
    }

    private ResponseEntity<ApiError> error(
            HttpStatus status,
            String code,
            String message,
            HttpServletRequest request
    ) {

        return ResponseEntity
                .status(status)
                .body(
                        new ApiError(
                                code,
                                message,
                                correlationId(request)
                        )
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