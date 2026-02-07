package com.hms.auth.infrastructure.persistence.adapter;

import com.hms.auth.domain.model.entity.RefreshToken;
import com.hms.auth.domain.model.entity.User;
import com.hms.auth.domain.port.out.RefreshTokenRepositoryPort;
import com.hms.auth.infrastructure.persistence.repository.JpaRefreshTokenRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter implementing RefreshTokenRepositoryPort using JPA.
 */
@Component
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final JpaRefreshTokenRepository jpaRefreshTokenRepository;

    public RefreshTokenRepositoryAdapter(JpaRefreshTokenRepository jpaRefreshTokenRepository) {
        this.jpaRefreshTokenRepository = jpaRefreshTokenRepository;
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        return jpaRefreshTokenRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findById(Long id) {
        return jpaRefreshTokenRepository.findById(id);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return jpaRefreshTokenRepository.findByToken(token);
    }

    @Override
    public List<RefreshToken> findByUser(User user) {
        return jpaRefreshTokenRepository.findByUser(user);
    }

    @Override
    public List<RefreshToken> findValidTokensByUser(User user) {
        return jpaRefreshTokenRepository.findValidTokensByUser(user, Instant.now());
    }

    @Override
    public List<RefreshToken> findByFamilyId(UUID familyId) {
        return jpaRefreshTokenRepository.findByFamilyId(familyId);
    }

    @Override
    @Transactional
    public int revokeAllByUser(User user, String reason) {
        return jpaRefreshTokenRepository.revokeAllByUser(user, reason, Instant.now());
    }

    @Override
    @Transactional
    public int revokeAllByFamilyId(UUID familyId, String reason) {
        return jpaRefreshTokenRepository.revokeAllByFamilyId(familyId, reason, Instant.now());
    }

    @Override
    @Transactional
    public int deleteExpiredTokensBefore(Instant before) {
        return jpaRefreshTokenRepository.deleteExpiredTokensBefore(before);
    }

    @Override
    public void deleteById(Long id) {
        jpaRefreshTokenRepository.deleteById(id);
    }

    @Override
    public long countActiveTokensByUser(User user) {
        return jpaRefreshTokenRepository.countActiveTokensByUser(user, Instant.now());
    }
}
