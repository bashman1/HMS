package com.hms.auth.infrastructure.persistence.adapter;

import com.hms.auth.domain.model.entity.User;
import com.hms.auth.domain.model.entity.VerificationToken;
import com.hms.auth.domain.model.entity.VerificationToken.TokenType;
import com.hms.auth.domain.port.out.VerificationTokenRepositoryPort;
import com.hms.auth.infrastructure.persistence.repository.JpaVerificationTokenRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Adapter implementing VerificationTokenRepositoryPort using JPA.
 */
@Component
public class VerificationTokenRepositoryAdapter implements VerificationTokenRepositoryPort {

    private final JpaVerificationTokenRepository jpaVerificationTokenRepository;

    public VerificationTokenRepositoryAdapter(JpaVerificationTokenRepository jpaVerificationTokenRepository) {
        this.jpaVerificationTokenRepository = jpaVerificationTokenRepository;
    }

    @Override
    public VerificationToken save(VerificationToken verificationToken) {
        return jpaVerificationTokenRepository.save(verificationToken);
    }

    @Override
    public Optional<VerificationToken> findById(Long id) {
        return jpaVerificationTokenRepository.findById(id);
    }

    @Override
    public Optional<VerificationToken> findByToken(String token) {
        return jpaVerificationTokenRepository.findByToken(token);
    }

    @Override
    public Optional<VerificationToken> findValidByToken(String token) {
        return jpaVerificationTokenRepository.findValidByToken(token, Instant.now());
    }

    @Override
    public List<VerificationToken> findByUser(User user) {
        return jpaVerificationTokenRepository.findByUser(user);
    }

    @Override
    public List<VerificationToken> findByUserAndType(User user, TokenType tokenType) {
        return jpaVerificationTokenRepository.findByUserAndTokenType(user, tokenType);
    }

    @Override
    public Optional<VerificationToken> findLatestValidByUserAndType(User user, TokenType tokenType) {
        return jpaVerificationTokenRepository.findLatestValidByUserAndTokenType(user, tokenType, Instant.now());
    }

    @Override
    @Transactional
    public int invalidateAllByUserAndType(User user, TokenType tokenType) {
        return jpaVerificationTokenRepository.invalidateAllByUserAndTokenType(user, tokenType, Instant.now());
    }

    @Override
    @Transactional
    public int deleteExpiredTokensBefore(Instant before) {
        return jpaVerificationTokenRepository.deleteExpiredTokensBefore(before);
    }

    @Override
    public void deleteById(Long id) {
        jpaVerificationTokenRepository.deleteById(id);
    }
}
