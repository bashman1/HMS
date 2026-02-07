package com.hms.auth.infrastructure.persistence.repository;

import com.hms.auth.domain.model.entity.User;
import com.hms.auth.domain.model.entity.VerificationToken;
import com.hms.auth.domain.model.entity.VerificationToken.TokenType;
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
 * JPA repository interface for VerificationToken entity.
 */
@Repository
public interface JpaVerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    /**
     * Finds a verification token by token string.
     */
    Optional<VerificationToken> findByToken(String token);

    /**
     * Finds a valid (non-expired, non-used) verification token by token string.
     */
    @Query("SELECT vt FROM VerificationToken vt WHERE vt.token = :token AND vt.expiresAt > :now AND vt.used = false")
    Optional<VerificationToken> findValidByToken(@Param("token") String token, @Param("now") Instant now);

    /**
     * Finds all verification tokens for a user.
     */
    List<VerificationToken> findByUser(User user);

    /**
     * Finds all verification tokens for a user of a specific type.
     */
    List<VerificationToken> findByUserAndTokenType(User user, TokenType tokenType);

    /**
     * Finds the latest valid token for a user of a specific type.
     */
    @Query("SELECT vt FROM VerificationToken vt WHERE vt.user = :user AND vt.tokenType = :tokenType AND vt.expiresAt > :now AND vt.used = false ORDER BY vt.createdAt DESC")
    Optional<VerificationToken> findLatestValidByUserAndTokenType(@Param("user") User user,
            @Param("tokenType") TokenType tokenType, @Param("now") Instant now);

    /**
     * Invalidates all tokens for a user of a specific type.
     */
    @Modifying
    @Query("UPDATE VerificationToken vt SET vt.used = true, vt.usedAt = :now WHERE vt.user = :user AND vt.tokenType = :tokenType AND vt.used = false")
    int invalidateAllByUserAndTokenType(@Param("user") User user, @Param("tokenType") TokenType tokenType,
            @Param("now") Instant now);

    /**
     * Deletes expired tokens older than the specified time.
     */
    @Modifying
    @Query("DELETE FROM VerificationToken vt WHERE vt.expiresAt < :before")
    int deleteExpiredTokensBefore(@Param("before") Instant before);
}
