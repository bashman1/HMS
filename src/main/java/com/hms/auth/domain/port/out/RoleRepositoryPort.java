package com.hms.auth.domain.port.out;

import com.hms.auth.domain.model.entity.Role;

import java.util.List;
import java.util.Optional;

/**
 * Port interface for role repository operations.
 * 
 * <p>
 * This interface defines the contract for role persistence operations
 * following the hexagonal architecture pattern.
 */
public interface RoleRepositoryPort {

    /**
     * Saves a role entity.
     *
     * @param role the role to save
     * @return the saved role
     */
    Role save(Role role);

    /**
     * Finds a role by ID.
     *
     * @param id the role ID
     * @return an Optional containing the role if found
     */
    Optional<Role> findById(Long id);

    /**
     * Finds a role by name.
     *
     * @param name the role name
     * @return an Optional containing the role if found
     */
    Optional<Role> findByName(String name);

    /**
     * Finds all roles.
     *
     * @return a list of all roles
     */
    List<Role> findAll();

    /**
     * Finds the default role for new users.
     *
     * @return an Optional containing the default role if found
     */
    Optional<Role> findDefaultRole();

    /**
     * Checks if a role exists with the given name.
     *
     * @param name the role name
     * @return true if a role exists with the name
     */
    boolean existsByName(String name);

    /**
     * Deletes a role by ID.
     *
     * @param id the role ID
     */
    void deleteById(Long id);
}
