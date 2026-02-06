package com.hms.auth.domain.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * User entity representing an authenticated user in the system.
 * 
 * <p>
 * This entity stores user credentials, profile information, and
 * security-related data such as account status, email verification, and
 * login attempt tracking. Supports various user types including
 * staff members (doctors, nurses, cashiers) and patients.
 */
@Entity
@Table(name = "users", schema = "auth", indexes = {
        @Index(name = "idx_users_email", columnList = "email", unique = true),
        @Index(name = "idx_users_username", columnList = "username", unique = true),
        @Index(name = "idx_users_user_type", columnList = "user_type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = { "password", "roles" })
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false, length = 20)
    @Builder.Default
    private UserType userType = UserType.STAFF;

    @Column(name = "email_verified", nullable = false)
    @Builder.Default
    private boolean emailVerified = false;

    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private boolean enabled = true;

    @Column(name = "account_non_expired", nullable = false)
    @Builder.Default
    private boolean accountNonExpired = true;

    @Column(name = "account_non_locked", nullable = false)
    @Builder.Default
    private boolean accountNonLocked = true;

    @Column(name = "credentials_non_expired", nullable = false)
    @Builder.Default
    private boolean credentialsNonExpired = true;

    @Column(name = "failed_login_attempts", nullable = false)
    @Builder.Default
    private int failedLoginAttempts = 0;

    @Column(name = "lock_time")
    private Instant lockTime;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @Column(name = "last_login_ip", length = 45)
    private String lastLoginIp;

    @Column(name = "password_changed_at")
    private Instant passwordChangedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", schema = "auth", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    /**
     * Adds a role to the user.
     *
     * @param role the role to add
     */
    public void addRole(Role role) {
        this.roles.add(role);
    }

    /**
     * Removes a role from the user.
     *
     * @param role the role to remove
     */
    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    /**
     * Checks if the user has a specific role.
     *
     * @param roleName the role name to check
     * @return true if the user has the role
     */
    public boolean hasRole(String roleName) {
        return roles.stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(roleName));
    }

    /**
     * Increments the failed login attempts counter.
     */
    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts++;
    }

    /**
     * Resets the failed login attempts counter.
     */
    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
        this.lockTime = null;
    }

    /**
     * Locks the user account.
     */
    public void lockAccount() {
        this.accountNonLocked = false;
        this.lockTime = Instant.now();
    }

    /**
     * Unlocks the user account.
     */
    public void unlockAccount() {
        this.accountNonLocked = true;
        this.lockTime = null;
        this.failedLoginAttempts = 0;
    }

    /**
     * Records a successful login.
     *
     * @param ipAddress the IP address of the login
     */
    public void recordSuccessfulLogin(String ipAddress) {
        this.lastLoginAt = Instant.now();
        this.lastLoginIp = ipAddress;
        this.failedLoginAttempts = 0;
    }

    /**
     * Gets the full name of the user.
     *
     * @return the full name
     */
    public String getFullName() {
        if (firstName == null && lastName == null) {
            return username;
        }
        return String.format("%s %s",
                firstName != null ? firstName : "",
                lastName != null ? lastName : "").trim();
    }

    /**
     * Checks if the account is fully active (enabled, verified, not locked, not
     * expired).
     *
     * @return true if the account is fully active
     */
    public boolean isFullyActive() {
        return enabled && emailVerified && accountNonLocked && accountNonExpired;
    }

    /**
     * Checks if this user is a staff member (doctor, nurse, cashier, lab tech,
     * etc.).
     *
     * @return true if the user is staff
     */
    public boolean isStaff() {
        return userType == UserType.STAFF;
    }

    /**
     * Checks if this user is a patient.
     *
     * @return true if the user is a patient
     */
    public boolean isPatient() {
        return userType == UserType.PATIENT;
    }

    /**
     * Checks if this user is an external user (vendor, partner, etc.).
     *
     * @return true if the user is external
     */
    public boolean isExternal() {
        return userType == UserType.EXTERNAL;
    }

    /**
     * Checks if this user is a system administrator.
     *
     * @return true if the user is a system admin
     */
    public boolean isSystemAdmin() {
        return hasRole(Role.ADMIN) && userType == UserType.SYSTEM;
    }

    /**
     * Gets a display-friendly user type label.
     *
     * @return the user type label
     */
    public String getUserTypeLabel() {
        return switch (userType) {
            case STAFF -> "Hospital Staff";
            case PATIENT -> "Patient";
            case EXTERNAL -> "External User";
            case SYSTEM -> "System Administrator";
        };
    }
}
