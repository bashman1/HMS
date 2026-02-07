package com.hms.auth.infrastructure.security.jwt;

import com.hms.auth.domain.exception.TokenException;
import com.hms.auth.domain.model.entity.User;
import com.hms.auth.infrastructure.config.properties.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * JWT token provider for creating and validating JWT tokens.
 * 
 * <p>
 * Handles access token and refresh token generation with configurable
 * expiration times and claims.
 */
@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(
                java.util.Base64.getEncoder().encodeToString(jwtProperties.secret().getBytes())));
    }

    /**
     * Generates an access token for a user.
     *
     * @param user the user
     * @return the access token
     */
    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(jwtProperties.accessTokenExpiration());

        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet());

        Set<String> permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> permission.getName())
                .collect(Collectors.toSet());

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getUuid().toString())
                .issuer(jwtProperties.issuer())
                .audience().add(jwtProperties.audience()).and()
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .claims(Map.of(
                        "username", user.getUsername(),
                        "email", user.getEmail(),
                        "roles", roles,
                        "permissions", permissions,
                        "type", "access"))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Generates a refresh token for a user.
     *
     * @param user     the user
     * @param familyId the token family ID for rotation tracking
     * @return the refresh token
     */
    public String generateRefreshToken(User user, UUID familyId) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(jwtProperties.refreshTokenExpiration());

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getUuid().toString())
                .issuer(jwtProperties.issuer())
                .audience().add(jwtProperties.audience()).and()
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .claims(Map.of(
                        "familyId", familyId.toString(),
                        "type", "refresh"))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Validates a JWT token.
     *
     * @param token the token to validate
     * @return true if the token is valid
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (TokenException e) {
            return false;
        }
    }

    /**
     * Parses and validates a JWT token.
     *
     * @param token the token to parse
     * @return the claims
     * @throws TokenException if the token is invalid
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.debug("JWT token expired: {}", e.getMessage());
            throw TokenException.expired();
        } catch (MalformedJwtException e) {
            log.debug("Invalid JWT token: {}", e.getMessage());
            throw TokenException.invalid("Malformed JWT token");
        } catch (SignatureException e) {
            log.debug("Invalid JWT signature: {}", e.getMessage());
            throw TokenException.invalid("Invalid JWT signature");
        } catch (UnsupportedJwtException e) {
            log.debug("Unsupported JWT token: {}", e.getMessage());
            throw TokenException.invalid("Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            log.debug("JWT claims string is empty: {}", e.getMessage());
            throw TokenException.invalid("JWT claims string is empty");
        }
    }

    /**
     * Extracts the user ID from a token.
     *
     * @param token the token
     * @return the user ID
     */
    public UUID getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return UUID.fromString(claims.getSubject());
    }

    /**
     * Extracts the token type from a token.
     *
     * @param token the token
     * @return the token type ("access" or "refresh")
     */
    public String getTokenType(String token) {
        Claims claims = parseToken(token);
        return claims.get("type", String.class);
    }

    /**
     * Extracts the family ID from a refresh token.
     *
     * @param token the refresh token
     * @return the family ID
     */
    public UUID getFamilyIdFromToken(String token) {
        Claims claims = parseToken(token);
        String familyId = claims.get("familyId", String.class);
        return familyId != null ? UUID.fromString(familyId) : null;
    }

    /**
     * Extracts the token ID (jti) from a token.
     *
     * @param token the token
     * @return the token ID
     */
    public String getTokenId(String token) {
        Claims claims = parseToken(token);
        return claims.getId();
    }

    /**
     * Gets the expiration time from a token.
     *
     * @param token the token
     * @return the expiration instant
     */
    public Instant getExpirationFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration().toInstant();
    }

    /**
     * Gets the access token expiration in seconds.
     *
     * @return expiration in seconds
     */
    public long getAccessTokenExpirationSeconds() {
        return jwtProperties.getAccessTokenExpirationSeconds();
    }

    /**
     * Gets the access token expiration instant from now.
     *
     * @return expiration instant
     */
    public Instant getAccessTokenExpirationInstant() {
        return Instant.now().plusMillis(jwtProperties.accessTokenExpiration());
    }

    /**
     * Gets the refresh token expiration instant from now.
     *
     * @return expiration instant
     */
    public Instant getRefreshTokenExpirationInstant() {
        return Instant.now().plusMillis(jwtProperties.refreshTokenExpiration());
    }
}
