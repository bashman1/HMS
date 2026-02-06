package com.hms.auth.domain.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * VerificationToken entity for email verification and password reset tokens.
 * 
 * <p>
 * This entity stores secure tokens used for:
 * <ul>
 * <li>Email verification during registration</li>
 * <li>Password reset requests</li>
 * <li>Other verification purposes</li>
 * </ul>
 */
@Entity
@Table(name = "verification_tokens", schema = "auth", indexes = {
        @Index(name = "idx_verification_tokens_token", columnList = "token", unique = true),
        @Index(name = "idx_verification_tokens_user_id", columnList = "user_id"),
        @Index(name = "idx_verification_tokens_type", columnList = "token_type"),
        @Index(name = "idx_verification_tokens_expires_at", columnList = "expires_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = "user")
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "token", nullable = false, unique = true, length = 255)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", nullable = false, length = 50)
    private TokenType tokenType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "used", nullable = false)
    @Builder.Default
    private boolean used = false;

    @Column(name = "used_at")
    private Instant usedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Token types.
     */
    public enum TokenType {
        EMAIL_VERIFICATION,
        PASSWORD_RESET,
        EMAIL_CHANGE,
        ACCOUNT_ACTIVATION
    }

    /**
     * Checks if the token is expired.
     *
     * @return true if the token is expired
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    /**
     * Checks if the token is valid (not expired and not used).
     *
     * @return true if the token is valid
     */
    public boolean isValid() {
        return !isExpired() && !used;
    }

    /**
     * Marks the token as used.
     */
    public void markAsUsed() {
        this.used = true;
        this.usedAt = Instant.now();
    }

    /**
     * Creates a new email verification token.
     *
     * @param user      the user
     * @param token     the token string
     * @param expiresAt the expiration time
     * @return the verification token
     */
    public static VerificationToken createEmailVerificationToken(User user, String token, Instant expiresAt) {
        return VerificationToken.builder()
                .user(user)
                .token(token)
                .tokenType(TokenType.EMAIL_VERIFICATION)
                .expiresAt(expiresAt)
                .build();
    }

    /**
     * Creates a new password reset token.
     *
     * @param user      the user
     * @param token     the token string
     * @param expiresAt the expiration time
     * @return the verification token
     */
    public static VerificationToken createPasswordResetToken(User user, String token, Instant expiresAt) {
        return VerificationToken.builder()
                .user(user)
                .token(token)
                .tokenType(TokenType.PASSWORD_RESET)
                .expiresAt(expiresAt)
                .build();
    }
}
