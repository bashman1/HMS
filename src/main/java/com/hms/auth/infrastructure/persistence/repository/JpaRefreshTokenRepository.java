package com.hms.auth.infrastructure.persistence.repository;

import com.hms.auth.domain.model.entity.RefreshToken;
import com.hms.auth.domain.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA repository interface for RefreshToken entity.
 */
@Repository
public interface JpaRefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Finds a refresh token by token string.
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Finds all refresh tokens for a user.
     */
    List<RefreshToken> findByUser(User user);

    /**
     * Finds all valid (non-revoked, non-expired) refresh tokens for a user.
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.user = :user AND rt.revoked = false AND rt.expiresAt > :now")
    List<RefreshToken> findValidTokensByUser(@Param("user") User user, @Param("now") Instant now);

    /**
     * Finds all refresh tokens in a token family.
     */
    List<RefreshToken> findByFamilyId(UUID familyId);

    /**
     * Revokes all refresh tokens for a user.
     */
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true, rt.revokedAt = :now, rt.revokedReason = :reason WHERE rt.user = :user AND rt.revoked = false")
    int revokeAllByUser(@Param("user") User user, @Param("reason") String reason, @Param("now") Instant now);

    /**
     * Revokes all refresh tokens in a token family.
     */
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true, rt.revokedAt = :now, rt.revokedReason = :reason WHERE rt.familyId = :familyId AND rt.revoked = false")
    int revokeAllByFamilyId(@Param("familyId") UUID familyId, @Param("reason") String reason,
            @Param("now") Instant now);

    /**
     * Deletes expired tokens older than the specified time.
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :before")
    int deleteExpiredTokensBefore(@Param("before") Instant before);

    /**
     * Counts active (non-revoked) tokens for a user.
     */
    @Query("SELECT COUNT(rt) FROM RefreshToken rt WHERE rt.user = :user AND rt.revoked = false AND rt.expiresAt > :now")
    long countActiveTokensByUser(@Param("user") User user, @Param("now") Instant now);
}
