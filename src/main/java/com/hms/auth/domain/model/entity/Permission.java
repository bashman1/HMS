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
 * Permission entity representing a specific action or access right in the
 * system.
 * 
 * <p>
 * Permissions are granular access controls that can be assigned to roles.
 * They follow the format: resource:action (e.g., "users:read", "users:write").
 */
@Entity
@Table(name = "permissions", schema = "auth", indexes = {
        @Index(name = "idx_permissions_name", columnList = "name", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = "roles")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "resource", length = 50)
    private String resource;

    @Column(name = "action", length = 50)
    private String action;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ManyToMany(mappedBy = "permissions")
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    /**
     * Creates a permission name from resource and action.
     *
     * @param resource the resource name
     * @param action   the action name
     * @return the combined permission name
     */
    public static String createPermissionName(String resource, String action) {
        return resource.toLowerCase() + ":" + action.toLowerCase();
    }

    /**
     * Common permission actions.
     */
    public static final String ACTION_CREATE = "create";
    public static final String ACTION_READ = "read";
    public static final String ACTION_UPDATE = "update";
    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_MANAGE = "manage";

    /**
     * Common resources.
     */
    public static final String RESOURCE_USERS = "users";
    public static final String RESOURCE_ROLES = "roles";
    public static final String RESOURCE_PERMISSIONS = "permissions";
}
