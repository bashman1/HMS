package com.hms.auth.infrastructure.security.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Service for managing blacklisted JWT tokens.
 * 
 * <p>
 * Uses an in-memory cache to store blacklisted token IDs.
 * In a production environment with multiple instances, this should
 * be replaced with a distributed cache like Redis.
 */
@Service
public class TokenBlacklistService {

    private static final Logger log = LoggerFactory.getLogger(TokenBlacklistService.class);

    // Cache for blacklisted tokens with automatic expiration
    private final Cache<String, Instant> blacklistedTokens;

    // Map to track token expiration times for cleanup
    private final ConcurrentMap<String, Instant> tokenExpirations;

    public TokenBlacklistService() {
        this.blacklistedTokens = Caffeine.newBuilder()
                .maximumSize(100_000)
                .expireAfterWrite(Duration.ofHours(24))
                .build();
        this.tokenExpirations = new ConcurrentHashMap<>();
    }

    /**
     * Adds a token to the blacklist.
     *
     * @param tokenId        the token ID (jti claim)
     * @param expirationTime the token's expiration time
     */
    public void blacklist(String tokenId, Instant expirationTime) {
        if (tokenId == null || expirationTime == null) {
            return;
        }

        // Only blacklist if token hasn't expired yet
        if (expirationTime.isAfter(Instant.now())) {
            blacklistedTokens.put(tokenId, expirationTime);
            tokenExpirations.put(tokenId, expirationTime);
            log.debug("Token blacklisted: {}", tokenId);
        }
    }

    /**
     * Checks if a token is blacklisted.
     *
     * @param tokenId the token ID (jti claim)
     * @return true if the token is blacklisted
     */
    public boolean isBlacklisted(String tokenId) {
        if (tokenId == null) {
            return false;
        }
        return blacklistedTokens.getIfPresent(tokenId) != null;
    }

    /**
     * Removes a token from the blacklist.
     *
     * @param tokenId the token ID
     */
    public void remove(String tokenId) {
        if (tokenId != null) {
            blacklistedTokens.invalidate(tokenId);
            tokenExpirations.remove(tokenId);
            log.debug("Token removed from blacklist: {}", tokenId);
        }
    }

    /**
     * Gets the count of blacklisted tokens.
     *
     * @return the count
     */
    public long getBlacklistSize() {
        return blacklistedTokens.estimatedSize();
    }

    /**
     * Cleans up expired tokens from the blacklist.
     * Runs every hour.
     */
    @Scheduled(fixedRate = 3600000) // 1 hour
    public void cleanupExpiredTokens() {
        log.debug("Starting blacklist cleanup...");
        Instant now = Instant.now();
        int removed = 0;

        for (var entry : tokenExpirations.entrySet()) {
            if (entry.getValue().isBefore(now)) {
                blacklistedTokens.invalidate(entry.getKey());
                tokenExpirations.remove(entry.getKey());
                removed++;
            }
        }

        if (removed > 0) {
            log.info("Cleaned up {} expired tokens from blacklist", removed);
        }

        // Trigger cache cleanup
        blacklistedTokens.cleanUp();
    }

    /**
     * Clears all blacklisted tokens.
     * Use with caution - mainly for testing.
     */
    public void clearAll() {
        blacklistedTokens.invalidateAll();
        tokenExpirations.clear();
        log.warn("All tokens cleared from blacklist");
    }
}
