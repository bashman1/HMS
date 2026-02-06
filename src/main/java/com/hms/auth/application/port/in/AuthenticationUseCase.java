package com.hms.auth.application.port.in;

import com.hms.auth.application.dto.request.*;
import com.hms.auth.application.dto.response.AuthResponse;
import com.hms.auth.application.dto.response.MessageResponse;

/**
 * Input port for authentication use cases.
 * 
 * <p>
 * Defines the contract for authentication operations following
 * the hexagonal architecture pattern.
 */
public interface AuthenticationUseCase {

    /**
     * Registers a new user.
     *
     * @param request   the registration request
     * @param ipAddress the client IP address
     * @return message response indicating success
     */
    MessageResponse register(RegisterRequest request, String ipAddress);

    /**
     * Authenticates a user and returns tokens.
     *
     * @param request   the login request
     * @param ipAddress the client IP address
     * @param userAgent the client user agent
     * @return authentication response with tokens
     */
    AuthResponse login(LoginRequest request, String ipAddress, String userAgent);

    /**
     * Refreshes an access token using a refresh token.
     *
     * @param request   the refresh token request
     * @param ipAddress the client IP address
     * @param userAgent the client user agent
     * @return authentication response with new tokens
     */
    AuthResponse refreshToken(RefreshTokenRequest request, String ipAddress, String userAgent);

    /**
     * Logs out a user by invalidating their tokens.
     *
     * @param accessToken  the access token to invalidate
     * @param refreshToken the refresh token to invalidate (optional)
     * @return message response indicating success
     */
    MessageResponse logout(String accessToken, String refreshToken);

    /**
     * Logs out a user from all devices by invalidating all their tokens.
     *
     * @param accessToken the current access token
     * @return message response indicating success
     */
    MessageResponse logoutAll(String accessToken);

    /**
     * Verifies a user's email address.
     *
     * @param token the verification token
     * @return message response indicating success
     */
    MessageResponse verifyEmail(String token);

    /**
     * Resends the email verification link.
     *
     * @param email the user's email address
     * @return message response indicating success
     */
    MessageResponse resendVerificationEmail(String email);

    /**
     * Initiates a password reset request.
     *
     * @param request the password reset request
     * @return message response indicating success
     */
    MessageResponse requestPasswordReset(PasswordResetRequest request);

    /**
     * Confirms a password reset with a new password.
     *
     * @param request the password reset confirmation request
     * @return message response indicating success
     */
    MessageResponse confirmPasswordReset(PasswordResetConfirmRequest request);

    /**
     * Changes the password for an authenticated user.
     *
     * @param request     the change password request
     * @param accessToken the user's access token
     * @return message response indicating success
     */
    MessageResponse changePassword(ChangePasswordRequest request, String accessToken);
}
