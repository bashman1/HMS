package com.hms.auth.infrastructure.persistence.repository;

import com.hms.auth.domain.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA repository interface for User entity.
 */
@Repository
public interface JpaUserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by email.
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by username.
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by UUID.
     */
    Optional<User> findByUuid(UUID uuid);

    /**
     * Finds a user by email or username.
     */
    @Query("SELECT u FROM User u WHERE u.email = :identifier OR u.username = :identifier")
    Optional<User> findByEmailOrUsername(@Param("identifier") String identifier);

    /**
     * Checks if a user exists with the given email.
     */
    boolean existsByEmail(String email);

    /**
     * Checks if a user exists with the given username.
     */
    boolean existsByUsername(String username);
}
