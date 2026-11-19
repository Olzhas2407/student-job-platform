package kz.studentjobs.security;

import kz.studentjobs.config.JwtProperties;

import org.junit.jupiter.api.Test;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import javax.crypto.SecretKey;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TokenServiceTest {

    @Test
    void issuedTokenCanBeDecoded() {

        JwtProperties properties =
                new JwtProperties(
                        "student-jobs-test-secret-2026-at-least-32-characters",
                        "student-jobs"
                );


        JwtConfig jwtConfig =
                new JwtConfig();


        SecretKey secretKey =
                jwtConfig.jwtSecretKey(
                        properties
                );


        JwtEncoder encoder =
                jwtConfig.jwtEncoder(
                        secretKey
                );


        JwtDecoder decoder =
                jwtConfig.jwtDecoder(
                        secretKey,
                        properties
                );


        TokenService tokenService =
                new TokenService(
                        encoder,
                        properties
                );


        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "manager",
                        "ignored",
                        List.of(
                                new SimpleGrantedAuthority(
                                        "ROLE_MANAGER"
                                )
                        )
                );


        String token =
                tokenService.issue(
                        authentication
                );


        Jwt decoded =
                decoder.decode(
                        token
                );


        assertThat(
                decoded.getSubject()
        ).isEqualTo(
                "manager"
        );


        assertThat(
                decoded.getClaimAsStringList(
                        "roles"
                )
        ).containsExactly(
                "MANAGER"
        );


        assertThat(
                decoded.getClaimAsString(
                        "iss"
                )
        ).isEqualTo(
                "student-jobs"
        );


        assertThat(
                decoded.getExpiresAt()
        ).isAfter(
                Instant.now()
        );
    }
}