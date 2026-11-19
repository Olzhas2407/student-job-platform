package kz.studentjobs.security;

import kz.studentjobs.config.JwtProperties;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class TokenService {

    public static final long EXPIRES_IN = 900;

    private final JwtEncoder encoder;
    private final JwtProperties properties;

    public TokenService(
            JwtEncoder encoder,
            JwtProperties properties
    ) {
        this.encoder = encoder;
        this.properties = properties;
    }

    public String issue(
            Authentication authentication
    ) {

        Instant now = Instant.now();

        List<String> roles =
                authentication
                        .getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .filter(authority ->
                                authority.startsWith("ROLE_")
                        )
                        .map(authority ->
                                authority.substring(5)
                        )
                        .toList();

        JwtClaimsSet claims =
                JwtClaimsSet.builder()
                        .issuer(properties.issuer())
                        .subject(authentication.getName())
                        .issuedAt(now)
                        .expiresAt(
                                now.plus(
                                        EXPIRES_IN,
                                        ChronoUnit.SECONDS
                                )
                        )
                        .claim("roles", roles)
                        .build();

        JwsHeader header =
                JwsHeader.with(
                        MacAlgorithm.HS256
                ).build();

        return encoder
                .encode(
                        JwtEncoderParameters.from(
                                header,
                                claims
                        )
                )
                .getTokenValue();
    }
}