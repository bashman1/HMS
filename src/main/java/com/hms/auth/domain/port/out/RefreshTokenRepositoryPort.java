package com.hms.auth.domain.port.out;

import com.hms.auth.domain.model.entity.RefreshToken;
import com.hms.auth.domain.model.entity.User;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port interface for refresh token repository operations.
 * 
 * <p>
 * This interface defines the contract for refresh token persistence operations
 * following the hexagonal architecture pattern.
 */
public interface RefreshTokenRepositoryPort {

    /**
     * Saves a refresh token entity.
     *
     * @param refreshToken the refresh token to save
     * @return the saved refresh token
     */
    RefreshToken save(RefreshToken refreshToken);

    /**
     * Finds a refresh token by ID.
     *
     * @param id the token ID
     * @return an Optional containing the token if found
     */
    Optional<RefreshToken> findById(Long id);

    /**
     * Finds a refresh token by token string.
     *
     * @param token the token string
     * @return an Optional containing the token if found
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Finds all refresh tokens for a user.
     *
     * @param user the user
     * @return a list of refresh tokens
     */
    List<RefreshToken> findByUser(User user);

    /**
     * Finds all valid (non-revoked, non-expired) refresh tokens for a user.
     *
     * @param user the user
     * @return a list of valid refresh tokens
     */
    List<RefreshToken> findValidTokensByUser(User user);

    /**
     * Finds all refresh tokens in a token family.
     *
     * @param familyId the family ID
     * @return a list of refresh tokens in the family
     */
    List<RefreshToken> findByFamilyId(UUID familyId);

    /**
     * Revokes all refresh tokens for a user.
     *
     * @param user   the user
     * @param reason the revocation reason
     * @return the number of tokens revoked
     */
    int revokeAllByUser(User user, String reason);

    /**
     * Revokes all refresh tokens in a token family.
     *
     * @param familyId the family ID
     * @param reason   the revocation reason
     * @return the number of tokens revoked
     */
    int revokeAllByFamilyId(UUID familyId, String reason);

    /**
     * Deletes expired tokens older than the specified time.
     *
     * @param before the cutoff time
     * @return the number of tokens deleted
     */
    int deleteExpiredTokensBefore(Instant before);

    /**
     * Deletes a refresh token by ID.
     *
     * @param id the token ID
     */
    void deleteById(Long id);

    /**
     * Counts active (non-revoked) tokens for a user.
     *
     * @param user the user
     * @return the count of active tokens
     */
    long countActiveTokensByUser(User user);
}
