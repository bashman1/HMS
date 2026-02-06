package com.hms.auth.application.service;

import com.hms.auth.application.dto.request.*;
import com.hms.auth.application.dto.response.AuthResponse;
import com.hms.auth.application.dto.response.MessageResponse;
import com.hms.auth.application.dto.response.UserResponse;
import com.hms.auth.application.port.in.AuthenticationUseCase;
import com.hms.auth.domain.exception.*;
import com.hms.auth.domain.model.entity.*;
import com.hms.auth.domain.model.entity.VerificationToken.TokenType;
import com.hms.auth.domain.port.out.*;
import com.hms.auth.infrastructure.config.properties.SecurityProperties;
import com.hms.auth.infrastructure.security.jwt.JwtTokenProvider;
import com.hms.auth.infrastructure.security.service.TokenBlacklistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of authentication use cases.
 * 
 * <p>
 * Provides comprehensive authentication functionality including
 * registration, login, token management, and password operations.
 */
@Service
@Transactional
public class AuthenticationService implements AuthenticationUseCase {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final VerificationTokenRepositoryPort verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistService tokenBlacklistService;
    private final AuthenticationManager authenticationManager;
    private final SecurityProperties securityProperties;
    private final EmailService emailService;
    private final PasswordValidationService passwordValidationService;
    private final BruteForceProtectionService bruteForceProtectionService;

    public AuthenticationService(
            UserRepositoryPort userRepository,
            RoleRepositoryPort roleRepository,
            RefreshTokenRepositoryPort refreshTokenRepository,
            VerificationTokenRepositoryPort verificationTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            TokenBlacklistService tokenBlacklistService,
            AuthenticationManager authenticationManager,
            SecurityProperties securityProperties,
            EmailService emailService,
            PasswordValidationService passwordValidationService,
            BruteForceProtectionService bruteForceProtectionService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenBlacklistService = tokenBlacklistService;
        this.authenticationManager = authenticationManager;
        this.securityProperties = securityProperties;
        this.emailService = emailService;
        this.passwordValidationService = passwordValidationService;
        this.bruteForceProtectionService = bruteForceProtectionService;
    }

    @Override
    public MessageResponse register(RegisterRequest request, String ipAddress) {
        log.info("Processing registration for email: {}", request.email());

        // Validate passwords match
        if (!request.passwordsMatch()) {
            throw new PasswordValidationException("Passwords do not match");
        }

        // Validate password strength
        passwordValidationService.validate(request.password());

        // Check if email already exists
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("email", request.email());
        }

        // Check if username already exists
        if (userRepository.existsByUsername(request.username())) {
            throw new UserAlreadyExistsException("username", request.username());
        }

        // Get default role
        Role defaultRole = roleRepository.findDefaultRole()
                .orElseGet(() -> roleRepository.findByName(Role.USER)
                        .orElseThrow(() -> new RuntimeException("Default role not found")));

        // Create user
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .phoneNumber(request.phoneNumber())
                .emailVerified(false)
                .enabled(true)
                .build();
        user.addRole(defaultRole);

        user = userRepository.save(user);
        log.info("User registered successfully: {}", user.getId());

        // Create and send verification token
        String token = generateSecureToken();
        VerificationToken verificationToken = VerificationToken.createEmailVerificationToken(
                user,
                token,
                Instant.now().plusMillis(86400000) // 24 hours
        );
        verificationTokenRepository.save(verificationToken);

        // Send verification email asynchronously
        emailService.sendVerificationEmail(user.getEmail(), user.getFullName(), token);

        return MessageResponse.success("Registration successful. Please check your email to verify your account.");
    }

    @Override
    public AuthResponse login(LoginRequest request, String ipAddress, String userAgent) {
        log.info("Processing login for: {}", request.usernameOrEmail());

        // Check brute force protection
        bruteForceProtectionService.checkAndThrowIfBlocked(ipAddress);

        try {
            // Authenticate
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.usernameOrEmail(),
                            request.password()));
        } catch (BadCredentialsException e) {
            bruteForceProtectionService.recordFailedAttempt(ipAddress);
            throw new InvalidCredentialsException();
        } catch (LockedException e) {
            throw new AccountLockedException("Account is locked. Please contact support.");
        }

        // Reset failed attempts on successful login
        bruteForceProtectionService.resetAttempts(ipAddress);

        // Get user
        User user = userRepository.findByEmailOrUsername(request.usernameOrEmail())
                .orElseThrow(() -> new UserNotFoundException("identifier", request.usernameOrEmail()));

        // Check if email is verified
        if (!user.isEmailVerified()) {
            throw new AuthException("Email not verified. Please verify your email first.",
                    org.springframework.http.HttpStatus.FORBIDDEN, "EMAIL_NOT_VERIFIED");
        }

        // Check if account is locked
        if (!user.isAccountNonLocked()) {
            var bruteForce = securityProperties.bruteForce();
            throw new AccountLockedException(user.getLockTime(), bruteForce.lockDuration());
        }

        // Record successful login
        user.recordSuccessfulLogin(ipAddress);
        userRepository.save(user);

        // Generate tokens
        return generateAuthResponse(user, ipAddress, userAgent);
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request, String ipAddress, String userAgent) {
        log.debug("Processing token refresh");

        // Find refresh token
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> TokenException.invalid("Refresh token not found"));

        // Validate token
        if (!refreshToken.isValid()) {
            if (refreshToken.isRevoked()) {
                // Potential token reuse attack - revoke entire family
                log.warn("Token reuse detected for family: {}", refreshToken.getFamilyId());
                refreshTokenRepository.revokeAllByFamilyId(
                        refreshToken.getFamilyId(),
                        RefreshToken.REVOKED_REUSE_DETECTED);
                throw TokenException.revoked();
            }
            throw TokenException.expired();
        }

        User user = refreshToken.getUser();

        // Rotate refresh token (revoke old, create new)
        String newRefreshTokenString = jwtTokenProvider.generateRefreshToken(user, refreshToken.getFamilyId());
        refreshToken.revokeAndReplace(newRefreshTokenString);
        refreshTokenRepository.save(refreshToken);

        // Create new refresh token entity
        RefreshToken newRefreshToken = RefreshToken.builder()
                .token(newRefreshTokenString)
                .familyId(refreshToken.getFamilyId())
                .user(user)
                .expiresAt(jwtTokenProvider.getRefreshTokenExpirationInstant())
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();
        refreshTokenRepository.save(newRefreshToken);

        // Generate new access token
        String accessToken = jwtTokenProvider.generateAccessToken(user);

        return AuthResponse.of(
                accessToken,
                newRefreshTokenString,
                jwtTokenProvider.getAccessTokenExpirationSeconds(),
                jwtTokenProvider.getAccessTokenExpirationInstant(),
                UserResponse.fromEntity(user));
    }

    @Override
    public MessageResponse logout(String accessToken, String refreshToken) {
        log.debug("Processing logout");

        // Blacklist access token
        if (accessToken != null && !accessToken.isBlank()) {
            try {
                String tokenId = jwtTokenProvider.getTokenId(accessToken);
                Instant expiration = jwtTokenProvider.getExpirationFromToken(accessToken);
                tokenBlacklistService.blacklist(tokenId, expiration);
            } catch (Exception e) {
                log.debug("Could not blacklist access token: {}", e.getMessage());
            }
        }

        // Revoke refresh token
        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenRepository.findByToken(refreshToken)
                    .ifPresent(token -> {
                        token.revoke(RefreshToken.REVOKED_LOGOUT);
                        refreshTokenRepository.save(token);
                    });
        }

        return MessageResponse.success("Logged out successfully");
    }

    @Override
    public MessageResponse logoutAll(String accessToken) {
        log.debug("Processing logout from all devices");

        UUID userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("id", userId.toString()));

        // Revoke all refresh tokens
        refreshTokenRepository.revokeAllByUser(user, RefreshToken.REVOKED_LOGOUT);

        // Blacklist current access token
        String tokenId = jwtTokenProvider.getTokenId(accessToken);
        Instant expiration = jwtTokenProvider.getExpirationFromToken(accessToken);
        tokenBlacklistService.blacklist(tokenId, expiration);

        return MessageResponse.success("Logged out from all devices successfully");
    }

    @Override
    public MessageResponse verifyEmail(String token) {
        log.info("Processing email verification");

        VerificationToken verificationToken = verificationTokenRepository.findValidByToken(token)
                .orElseThrow(() -> TokenException.invalid("Invalid or expired verification token"));

        if (verificationToken.getTokenType() != TokenType.EMAIL_VERIFICATION) {
            throw TokenException.invalid("Invalid token type");
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        verificationToken.markAsUsed();
        verificationTokenRepository.save(verificationToken);

        log.info("Email verified for user: {}", user.getId());
        return MessageResponse.success("Email verified successfully. You can now log in.");
    }

    @Override
    public MessageResponse resendVerificationEmail(String email) {
        log.info("Resending verification email to: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("email", email));

        if (user.isEmailVerified()) {
            return MessageResponse.success("Email is already verified.");
        }

        // Invalidate existing tokens
        verificationTokenRepository.invalidateAllByUserAndType(user, TokenType.EMAIL_VERIFICATION);

        // Create new token
        String token = generateSecureToken();
        VerificationToken verificationToken = VerificationToken.createEmailVerificationToken(
                user,
                token,
                Instant.now().plusMillis(86400000) // 24 hours
        );
        verificationTokenRepository.save(verificationToken);

        // Send email
        emailService.sendVerificationEmail(user.getEmail(), user.getFullName(), token);

        return MessageResponse.success("Verification email sent. Please check your inbox.");
    }

    @Override
    public MessageResponse requestPasswordReset(PasswordResetRequest request) {
        log.info("Processing password reset request for: {}", request.email());

        // Always return success to prevent email enumeration
        userRepository.findByEmail(request.email()).ifPresent(user -> {
            // Invalidate existing tokens
            verificationTokenRepository.invalidateAllByUserAndType(user, TokenType.PASSWORD_RESET);

            // Create new token
            String token = generateSecureToken();
            VerificationToken resetToken = VerificationToken.createPasswordResetToken(
                    user,
                    token,
                    Instant.now().plusMillis(3600000) // 1 hour
            );
            verificationTokenRepository.save(resetToken);

            // Send email
            emailService.sendPasswordResetEmail(user.getEmail(), user.getFullName(), token);
        });

        return MessageResponse.success("If an account exists with this email, a password reset link has been sent.");
    }

    @Override
    public MessageResponse confirmPasswordReset(PasswordResetConfirmRequest request) {
        log.info("Processing password reset confirmation");

        if (!request.passwordsMatch()) {
            throw new PasswordValidationException("Passwords do not match");
        }

        passwordValidationService.validate(request.newPassword());

        VerificationToken resetToken = verificationTokenRepository.findValidByToken(request.token())
                .orElseThrow(() -> TokenException.invalid("Invalid or expired reset token"));

        if (resetToken.getTokenType() != TokenType.PASSWORD_RESET) {
            throw TokenException.invalid("Invalid token type");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setPasswordChangedAt(Instant.now());
        userRepository.save(user);

        resetToken.markAsUsed();
        verificationTokenRepository.save(resetToken);

        // Revoke all refresh tokens for security
        refreshTokenRepository.revokeAllByUser(user, RefreshToken.REVOKED_PASSWORD_CHANGE);

        log.info("Password reset completed for user: {}", user.getId());
        return MessageResponse.success("Password reset successfully. Please log in with your new password.");
    }

    @Override
    public MessageResponse changePassword(ChangePasswordRequest request, String accessToken) {
        log.info("Processing password change");

        if (!request.passwordsMatch()) {
            throw new PasswordValidationException("Passwords do not match");
        }

        if (!request.isNewPasswordDifferent()) {
            throw new PasswordValidationException("New password must be different from current password");
        }

        passwordValidationService.validate(request.newPassword());

        UUID userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("id", userId.toString()));

        // Verify current password
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setPasswordChangedAt(Instant.now());
        userRepository.save(user);

        // Revoke all refresh tokens except current session
        refreshTokenRepository.revokeAllByUser(user, RefreshToken.REVOKED_PASSWORD_CHANGE);

        log.info("Password changed for user: {}", user.getId());
        return MessageResponse.success("Password changed successfully.");
    }

    /**
     * Generates authentication response with tokens.
     */
    private AuthResponse generateAuthResponse(User user, String ipAddress, String userAgent) {
        // Generate tokens
        UUID familyId = UUID.randomUUID();
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshTokenString = jwtTokenProvider.generateRefreshToken(user, familyId);

        // Save refresh token
        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenString)
                .familyId(familyId)
                .user(user)
                .expiresAt(jwtTokenProvider.getRefreshTokenExpirationInstant())
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();
        refreshTokenRepository.save(refreshToken);

        return AuthResponse.of(
                accessToken,
                refreshTokenString,
                jwtTokenProvider.getAccessTokenExpirationSeconds(),
                jwtTokenProvider.getAccessTokenExpirationInstant(),
                UserResponse.fromEntity(user));
    }

    /**
     * Generates a secure random token.
     */
    private String generateSecureToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
