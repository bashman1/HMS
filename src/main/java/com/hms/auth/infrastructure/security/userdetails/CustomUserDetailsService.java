package com.hms.auth.infrastructure.security.userdetails;

import com.hms.auth.domain.exception.UserNotFoundException;
import com.hms.auth.domain.model.entity.User;
import com.hms.auth.domain.port.out.UserRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Custom UserDetailsService implementation for Spring Security.
 * 
 * <p>
 * Loads user details from the database for authentication.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserRepositoryPort userRepository;

    public CustomUserDetailsService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user by username or email.
     *
     * @param usernameOrEmail the username or email
     * @return the user details
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        log.debug("Loading user by username or email: {}", usernameOrEmail);

        User user = userRepository.findByEmailOrUsername(usernameOrEmail)
                .orElseThrow(() -> {
                    log.debug("User not found: {}", usernameOrEmail);
                    return new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail);
                });

        return new CustomUserDetails(user);
    }

    /**
     * Loads a user by ID.
     *
     * @param userId the user ID
     * @return the user details
     * @throws UserNotFoundException if user not found
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(UUID userId) {
        log.debug("Loading user by ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.debug("User not found with ID: {}", userId);
                    return new UserNotFoundException("id", userId.toString());
                });

        return new CustomUserDetails(user);
    }

    /**
     * Loads a user entity by ID.
     *
     * @param userId the user ID
     * @return the user entity
     * @throws UserNotFoundException if user not found
     */
    @Transactional(readOnly = true)
    public User loadUserEntityById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("id", userId.toString()));
    }
}
