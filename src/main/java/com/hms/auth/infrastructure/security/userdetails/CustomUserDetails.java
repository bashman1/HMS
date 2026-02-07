package com.hms.auth.infrastructure.security.userdetails;

import com.hms.auth.domain.model.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Custom UserDetails implementation wrapping the User entity.
 * 
 * <p>
 * Provides Spring Security integration with the domain User entity.
 */
public class CustomUserDetails implements UserDetails {

    private final User user;
    private final Set<GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.user = user;
        this.authorities = buildAuthorities(user);
    }

    /**
     * Builds the set of granted authorities from user roles and permissions.
     *
     * @param user the user entity
     * @return the set of authorities
     */
    private Set<GrantedAuthority> buildAuthorities(User user) {
        Set<GrantedAuthority> authorities = new HashSet<>();

        // Add role-based authorities
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));

            // Add permission-based authorities
            role.getPermissions()
                    .forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.getName())));
        });

        return authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return user.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

    /**
     * Gets the underlying User entity.
     *
     * @return the user entity
     */
    public User getUser() {
        return user;
    }

    /**
     * Gets the user ID.
     *
     * @return the user ID
     */
    public Long getId() {
        return user.getId();
    }

    /**
     * Gets the user's email.
     *
     * @return the email
     */
    public String getEmail() {
        return user.getEmail();
    }

    /**
     * Checks if the user's email is verified.
     *
     * @return true if email is verified
     */
    public boolean isEmailVerified() {
        return user.isEmailVerified();
    }

    /**
     * Checks if the user has a specific role.
     *
     * @param roleName the role name
     * @return true if the user has the role
     */
    public boolean hasRole(String roleName) {
        return authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + roleName));
    }

    /**
     * Checks if the user has a specific permission.
     *
     * @param permission the permission name
     * @return true if the user has the permission
     */
    public boolean hasPermission(String permission) {
        return authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals(permission));
    }
}
