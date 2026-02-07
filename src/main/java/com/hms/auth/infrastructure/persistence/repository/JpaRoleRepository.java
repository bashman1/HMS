package com.hms.auth.infrastructure.persistence.repository;

import com.hms.auth.domain.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA repository interface for Role entity.
 */
@Repository
public interface JpaRoleRepository extends JpaRepository<Role, Long> {

    /**
     * Finds a role by name.
     */
    Optional<Role> findByName(String name);

    /**
     * Finds the default role for new users.
     */
    Optional<Role> findByIsDefaultTrue();

    /**
     * Checks if a role exists with the given name.
     */
    boolean existsByName(String name);
}
