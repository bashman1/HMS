package com.hms.auth.infrastructure.security.jwt;

import com.hms.auth.domain.exception.TokenException;
import com.hms.auth.infrastructure.security.service.TokenBlacklistService;
import com.hms.auth.infrastructure.security.userdetails.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * JWT authentication filter that processes JWT tokens from requests.
 * 
 * <p>
 * This filter extracts the JWT token from the Authorization header,
 * validates it, and sets up the Spring Security context.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    public JwtAuthenticationFilter(
            JwtTokenProvider jwtTokenProvider,
            CustomUserDetailsService userDetailsService,
            TokenBlacklistService tokenBlacklistService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Set correlation ID for request tracing
        String correlationId = request.getHeader("X-Correlation-Id");
        if (!StringUtils.hasText(correlationId)) {
            correlationId = UUID.randomUUID().toString();
        }
        MDC.put("correlationId", correlationId);
        response.setHeader("X-Correlation-Id", correlationId);

        try {
            String jwt = extractJwtFromRequest(request);

            if (StringUtils.hasText(jwt)) {
                processJwtAuthentication(jwt, request);
            }

            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

    /**
     * Extracts the JWT token from the Authorization header.
     *
     * @param request the HTTP request
     * @return the JWT token or null if not present
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    /**
     * Processes JWT authentication.
     *
     * @param jwt     the JWT token
     * @param request the HTTP request
     */
    private void processJwtAuthentication(String jwt, HttpServletRequest request) {
        try {
            // Validate token
            if (!jwtTokenProvider.validateToken(jwt)) {
                log.debug("Invalid JWT token");
                return;
            }

            // Check if token is blacklisted
            String tokenId = jwtTokenProvider.getTokenId(jwt);
            if (tokenBlacklistService.isBlacklisted(tokenId)) {
                log.debug("JWT token is blacklisted: {}", tokenId);
                return;
            }

            // Verify it's an access token
            String tokenType = jwtTokenProvider.getTokenType(jwt);
            if (!"access".equals(tokenType)) {
                log.debug("Token is not an access token");
                return;
            }

            // Get user UUID and load user details
            UUID userUuid = jwtTokenProvider.getUserIdFromToken(jwt);
            UserDetails userDetails = userDetailsService.loadUserByUuid(userUuid);

            // Create authentication token
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("Successfully authenticated user: {}", userUuid);

        } catch (TokenException e) {
            log.debug("JWT authentication failed: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Could not set user authentication in security context", e);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        // Skip filter for public endpoints
        return path.startsWith("/auth/login") ||
                path.startsWith("/auth/register") ||
                path.startsWith("/auth/verify-email") ||
                path.startsWith("/auth/password-reset") ||
                path.startsWith("/auth/refresh-token") ||
                path.startsWith("/actuator/health") ||
                path.startsWith("/api-docs") ||
                path.startsWith("/swagger-ui");
    }
}
