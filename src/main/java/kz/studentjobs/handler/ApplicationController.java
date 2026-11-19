package kz.studentjobs.handler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;

import kz.studentjobs.config.ApplicationDetails;
import kz.studentjobs.config.ApplicationService;
import kz.studentjobs.domain.ApplicationId;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationController {

    private final ApplicationService service;

    public ApplicationController(ApplicationService service) {
        this.service = service;
    }

    @Operation(summary = "Create application")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Application created"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Duplicate business key"
            )
    })
    @PostMapping
    public ResponseEntity<ApplicationResponse> create(
            @Valid @RequestBody CreateApplicationRequest request
    ) {

        ApplicationId id = ApplicationId.newId();

        ApplicationDetails created = service.register(
                id,
                request.businessKey(),
                request.title()
        );

        ApplicationResponse response = toResponse(created);

        URI location = URI.create(
                "/api/v1/applications/" + created.id()
        );

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @Operation(summary = "Get application by UUID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Application found"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Application not found"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getById(
            @PathVariable UUID id
    ) {

        ApplicationDetails application =
                service.findById(id);

        return ResponseEntity.ok(
                toResponse(application)
        );
    }
    @Operation(summary = "Submit application")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Application submitted"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Valid token required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Not allowed for your role"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Application not found"
            )
    })
    @PostMapping("/{id}/submit")
    public ResponseEntity<ApplicationResponse> submit(
            @PathVariable UUID id,
            Authentication authentication
    ) {

        ApplicationDetails updated =
                service.submit(
                        id,
                        authentication.getName()
                );

        return ResponseEntity.ok(
                toResponse(updated)
        );
    }
    private ApplicationResponse toResponse(
            ApplicationDetails application
    ) {
        return new ApplicationResponse(
                application.id(),
                application.businessKey(),
                application.status(),
                application.title()
        );
    }
}