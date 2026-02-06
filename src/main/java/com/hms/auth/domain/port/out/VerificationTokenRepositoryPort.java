package com.hms.auth.domain.port.out;

import com.hms.auth.domain.model.entity.User;
import com.hms.auth.domain.model.entity.VerificationToken;
import com.hms.auth.domain.model.entity.VerificationToken.TokenType;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port interface for verification token repository operations.
 * 
 * <p>
 * This interface defines the contract for verification token persistence
 * operations
 * following the hexagonal architecture pattern.
 */
public interface VerificationTokenRepositoryPort {

    /**
     * Saves a verification token entity.
     *
     * @param verificationToken the verification token to save
     * @return the saved verification token
     */
    VerificationToken save(VerificationToken verificationToken);

    /**
     * Finds a verification token by ID.
     *
     * @param id the token ID
     * @return an Optional containing the token if found
     */
    Optional<VerificationToken> findById(UUID id);

    /**
     * Finds a verification token by token string.
     *
     * @param token the token string
     * @return an Optional containing the token if found
     */
    Optional<VerificationToken> findByToken(String token);

    /**
     * Finds a valid (non-expired, non-used) verification token by token string.
     *
     * @param token the token string
     * @return an Optional containing the token if found and valid
     */
    Optional<VerificationToken> findValidByToken(String token);

    /**
     * Finds all verification tokens for a user.
     *
     * @param user the user
     * @return a list of verification tokens
     */
    List<VerificationToken> findByUser(User user);

    /**
     * Finds all verification tokens for a user of a specific type.
     *
     * @param user      the user
     * @param tokenType the token type
     * @return a list of verification tokens
     */
    List<VerificationToken> findByUserAndType(User user, TokenType tokenType);

    /**
     * Finds the latest valid token for a user of a specific type.
     *
     * @param user      the user
     * @param tokenType the token type
     * @return an Optional containing the token if found
     */
    Optional<VerificationToken> findLatestValidByUserAndType(User user, TokenType tokenType);

    /**
     * Invalidates all tokens for a user of a specific type.
     *
     * @param user      the user
     * @param tokenType the token type
     * @return the number of tokens invalidated
     */
    int invalidateAllByUserAndType(User user, TokenType tokenType);

    /**
     * Deletes expired tokens older than the specified time.
     *
     * @param before the cutoff time
     * @return the number of tokens deleted
     */
    int deleteExpiredTokensBefore(Instant before);

    /**
     * Deletes a verification token by ID.
     *
     * @param id the token ID
     */
    void deleteById(UUID id);
}
