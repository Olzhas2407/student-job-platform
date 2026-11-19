package kz.studentjobs.handler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MeController {

    @Operation(summary = "Get current authenticated user")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Current user"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Valid token required"
            )
    })
    @GetMapping("/me")
    public MeResponse me(
            Authentication authentication
    ) {

        List<String> authorities =
                authentication
                        .getAuthorities()
                        .stream()
                        .map(
                                GrantedAuthority::getAuthority
                        )
                        .toList();

        return new MeResponse(
                authentication.getName(),
                authorities
        );
    }
}