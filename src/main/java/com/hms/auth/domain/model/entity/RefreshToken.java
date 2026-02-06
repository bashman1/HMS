package com.hms.auth.domain.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * RefreshToken entity for storing refresh tokens in the database.
 * 
 * <p>
 * Refresh tokens are long-lived tokens used to obtain new access tokens
 * without requiring the user to re-authenticate. They are stored in the
 * database
 * to enable token revocation and family rotation.
 */
@Entity
@Table(name = "refresh_tokens", schema = "auth", indexes = {
        @Index(name = "idx_refresh_tokens_token", columnList = "token", unique = true),
        @Index(name = "idx_refresh_tokens_user_id", columnList = "user_id"),
        @Index(name = "idx_refresh_tokens_family_id", columnList = "family_id"),
        @Index(name = "idx_refresh_tokens_expires_at", columnList = "expires_at"),
        @Index(name = "idx_refresh_tokens_uuid", columnList = "uuid")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = "user")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "uuid", columnDefinition = "uuid", nullable = false, unique = true)
    @Builder.Default
    private UUID uuid = UUID.randomUUID();

    @Column(name = "token", nullable = false, unique = true, length = 500)
    private String token;

    @Column(name = "family_id", nullable = false)
    private UUID familyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked", nullable = false)
    @Builder.Default
    private boolean revoked = false;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "revoked_reason", length = 255)
    private String revokedReason;

    @Column(name = "replaced_by_token", length = 500)
    private String replacedByToken;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "device_info", length = 255)
    private String deviceInfo;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Checks if the token is expired.
     *
     * @return true if the token is expired
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    /**
     * Checks if the token is valid (not expired and not revoked).
     *
     * @return true if the token is valid
     */
    public boolean isValid() {
        return !isExpired() && !revoked;
    }

    /**
     * Revokes this token.
     *
     * @param reason the reason for revocation
     */
    public void revoke(String reason) {
        this.revoked = true;
        this.revokedAt = Instant.now();
        this.revokedReason = reason;
    }

    /**
     * Revokes this token and marks it as replaced.
     *
     * @param newToken the new token that replaces this one
     */
    public void revokeAndReplace(String newToken) {
        revoke("Token rotation");
        this.replacedByToken = newToken;
    }

    /**
     * Revocation reasons.
     */
    public static final String REVOKED_LOGOUT = "User logout";
    public static final String REVOKED_PASSWORD_CHANGE = "Password changed";
    public static final String REVOKED_SECURITY = "Security concern";
    public static final String REVOKED_ADMIN = "Revoked by administrator";
    public static final String REVOKED_ROTATION = "Token rotation";
    public static final String REVOKED_REUSE_DETECTED = "Token reuse detected";
}
