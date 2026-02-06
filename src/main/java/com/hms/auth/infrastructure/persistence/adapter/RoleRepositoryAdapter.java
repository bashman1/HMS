package com.hms.auth.infrastructure.persistence.adapter;

import com.hms.auth.domain.model.entity.Role;
import com.hms.auth.domain.port.out.RoleRepositoryPort;
import com.hms.auth.infrastructure.persistence.repository.JpaRoleRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter implementing RoleRepositoryPort using JPA.
 */
@Component
public class RoleRepositoryAdapter implements RoleRepositoryPort {

    private final JpaRoleRepository jpaRoleRepository;

    public RoleRepositoryAdapter(JpaRoleRepository jpaRoleRepository) {
        this.jpaRoleRepository = jpaRoleRepository;
    }

    @Override
    public Role save(Role role) {
        return jpaRoleRepository.save(role);
    }

    @Override
    public Optional<Role> findById(UUID id) {
        return jpaRoleRepository.findById(id);
    }

    @Override
    public Optional<Role> findByName(String name) {
        return jpaRoleRepository.findByName(name);
    }

    @Override
    public List<Role> findAll() {
        return jpaRoleRepository.findAll();
    }

    @Override
    public Optional<Role> findDefaultRole() {
        return jpaRoleRepository.findByIsDefaultTrue();
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRoleRepository.existsByName(name);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRoleRepository.deleteById(id);
    }
}
