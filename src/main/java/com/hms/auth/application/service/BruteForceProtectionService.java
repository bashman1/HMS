package com.hms.auth.application.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hms.auth.domain.exception.RateLimitExceededException;
import com.hms.auth.infrastructure.config.properties.SecurityProperties;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service for brute force attack protection.
 * 
 * <p>
 * Implements rate limiting and account lockout mechanisms
 * to protect against brute force login attempts.
 */
@Service
public class BruteForceProtectionService {

    private static final Logger log = LoggerFactory.getLogger(BruteForceProtectionService.class);

    private final SecurityProperties securityProperties;

    // Cache for tracking failed login attempts per IP
    private final Cache<String, AtomicInteger> failedAttemptsCache;

    // Cache for rate limiting buckets per IP
    private final Cache<String, Bucket> rateLimitBuckets;

    public BruteForceProtectionService(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;

        var bruteForce = securityProperties.bruteForce();

        this.failedAttemptsCache = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(Duration.ofSeconds(bruteForce.lockDuration()))
                .build();

        this.rateLimitBuckets = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterAccess(Duration.ofMinutes(10))
                .build();
    }

    /**
     * Checks if the IP is blocked and throws exception if so.
     *
     * @param ipAddress the IP address to check
     * @throws RateLimitExceededException if the IP is blocked
     */
    public void checkAndThrowIfBlocked(String ipAddress) {
        if (!securityProperties.rateLimiting().enabled()) {
            return;
        }

        // Check rate limit
        Bucket bucket = getRateLimitBucket(ipAddress);
        if (!bucket.tryConsume(1)) {
            var loginConfig = securityProperties.rateLimiting().login();
            log.warn("Rate limit exceeded for IP: {}", ipAddress);
            throw new RateLimitExceededException(loginConfig.refillDuration());
        }

        // Check failed attempts
        AtomicInteger attempts = failedAttemptsCache.getIfPresent(ipAddress);
        if (attempts != null && attempts.get() >= securityProperties.bruteForce().maxAttempts()) {
            log.warn("IP blocked due to too many failed attempts: {}", ipAddress);
            throw new RateLimitExceededException(
                    "Too many failed login attempts. Please try again later.",
                    securityProperties.bruteForce().lockDuration());
        }
    }

    /**
     * Records a failed login attempt for an IP address.
     *
     * @param ipAddress the IP address
     */
    public void recordFailedAttempt(String ipAddress) {
        AtomicInteger attempts = failedAttemptsCache.get(ipAddress, k -> new AtomicInteger(0));
        int currentAttempts = attempts.incrementAndGet();

        log.debug("Failed login attempt {} for IP: {}", currentAttempts, ipAddress);

        if (currentAttempts >= securityProperties.bruteForce().maxAttempts()) {
            log.warn("IP {} blocked after {} failed attempts", ipAddress, currentAttempts);
        }
    }

    /**
     * Resets failed attempts for an IP address after successful login.
     *
     * @param ipAddress the IP address
     */
    public void resetAttempts(String ipAddress) {
        failedAttemptsCache.invalidate(ipAddress);
        log.debug("Reset failed attempts for IP: {}", ipAddress);
    }

    /**
     * Gets the number of failed attempts for an IP address.
     *
     * @param ipAddress the IP address
     * @return the number of failed attempts
     */
    public int getFailedAttempts(String ipAddress) {
        AtomicInteger attempts = failedAttemptsCache.getIfPresent(ipAddress);
        return attempts != null ? attempts.get() : 0;
    }

    /**
     * Checks if an IP address is currently blocked.
     *
     * @param ipAddress the IP address
     * @return true if blocked
     */
    public boolean isBlocked(String ipAddress) {
        AtomicInteger attempts = failedAttemptsCache.getIfPresent(ipAddress);
        return attempts != null && attempts.get() >= securityProperties.bruteForce().maxAttempts();
    }

    /**
     * Gets or creates a rate limit bucket for an IP address.
     */
    private Bucket getRateLimitBucket(String ipAddress) {
        return rateLimitBuckets.get(ipAddress, this::createNewBucket);
    }

    /**
     * Creates a new rate limit bucket.
     */
    private Bucket createNewBucket(String ipAddress) {
        var loginConfig = securityProperties.rateLimiting().login();

        Bandwidth limit = Bandwidth.classic(
                loginConfig.capacity(),
                Refill.greedy(loginConfig.refillTokens(), Duration.ofSeconds(loginConfig.refillDuration())));

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Gets the remaining attempts before lockout.
     *
     * @param ipAddress the IP address
     * @return remaining attempts
     */
    public int getRemainingAttempts(String ipAddress) {
        int failed = getFailedAttempts(ipAddress);
        return Math.max(0, securityProperties.bruteForce().maxAttempts() - failed);
    }
}
