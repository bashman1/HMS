package com.hms.auth.domain.port.out;

import com.hms.auth.domain.model.entity.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Port interface for user repository operations.
 * 
 * <p>
 * This interface defines the contract for user persistence operations
 * following the hexagonal architecture pattern. The implementation is
 * provided by the infrastructure layer.
 */
public interface UserRepositoryPort {

    /**
     * Saves a user entity.
     *
     * @param user the user to save
     * @return the saved user
     */
    User save(User user);

    /**
     * Finds a user by ID.
     *
     * @param id the user ID
     * @return an Optional containing the user if found
     */
    Optional<User> findById(Long id);

    /**
     * Finds a user by UUID.
     *
     * @param uuid the user UUID
     * @return an Optional containing the user if found
     */
    Optional<User> findByUuid(UUID uuid);

    /**
     * Finds a user by email.
     *
     * @param email the email address
     * @return an Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by username.
     *
     * @param username the username
     * @return an Optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by email or username.
     *
     * @param emailOrUsername the email or username
     * @return an Optional containing the user if found
     */
    Optional<User> findByEmailOrUsername(String emailOrUsername);

    /**
     * Checks if a user exists with the given email.
     *
     * @param email the email address
     * @return true if a user exists with the email
     */
    boolean existsByEmail(String email);

    /**
     * Checks if a user exists with the given username.
     *
     * @param username the username
     * @return true if a user exists with the username
     */
    boolean existsByUsername(String username);

    /**
     * Deletes a user by ID.
     *
     * @param id the user ID
     */
    void deleteById(Long id);

    /**
     * Counts all users.
     *
     * @return the total number of users
     */
    long count();
}
