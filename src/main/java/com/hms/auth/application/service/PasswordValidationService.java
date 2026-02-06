package com.hms.auth.application.service;

import com.hms.auth.domain.exception.PasswordValidationException;
import com.hms.auth.infrastructure.config.properties.SecurityProperties;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Service for validating password strength.
 * 
 * <p>
 * Validates passwords against configurable security requirements.
 */
@Service
public class PasswordValidationService {

    private final SecurityProperties.PasswordProperties passwordProperties;

    // Precompiled patterns for performance
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_PATTERN = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]");

    public PasswordValidationService(SecurityProperties securityProperties) {
        this.passwordProperties = securityProperties.password();
    }

    /**
     * Validates a password against security requirements.
     *
     * @param password the password to validate
     * @throws PasswordValidationException if validation fails
     */
    public void validate(String password) {
        List<String> violations = new ArrayList<>();

        if (password == null || password.isBlank()) {
            throw new PasswordValidationException("Password cannot be empty");
        }

        // Check minimum length
        if (password.length() < passwordProperties.minLength()) {
            violations.add("Password must be at least " + passwordProperties.minLength() + " characters long");
        }

        // Check for uppercase
        if (passwordProperties.requireUppercase() && !UPPERCASE_PATTERN.matcher(password).find()) {
            violations.add("Password must contain at least one uppercase letter");
        }

        // Check for lowercase
        if (passwordProperties.requireLowercase() && !LOWERCASE_PATTERN.matcher(password).find()) {
            violations.add("Password must contain at least one lowercase letter");
        }

        // Check for digit
        if (passwordProperties.requireDigit() && !DIGIT_PATTERN.matcher(password).find()) {
            violations.add("Password must contain at least one digit");
        }

        // Check for special character
        if (passwordProperties.requireSpecial() && !SPECIAL_PATTERN.matcher(password).find()) {
            violations.add("Password must contain at least one special character");
        }

        // Check for common passwords (basic check)
        if (isCommonPassword(password)) {
            violations.add("Password is too common. Please choose a stronger password");
        }

        if (!violations.isEmpty()) {
            throw new PasswordValidationException(violations);
        }
    }

    /**
     * Checks if the password is a common/weak password.
     */
    private boolean isCommonPassword(String password) {
        String lowerPassword = password.toLowerCase();
        return COMMON_PASSWORDS.stream().anyMatch(lowerPassword::contains);
    }

    /**
     * List of common passwords to reject.
     */
    private static final List<String> COMMON_PASSWORDS = List.of(
            "password", "123456", "qwerty", "abc123", "letmein",
            "welcome", "admin", "login", "passw0rd", "master",
            "hello", "dragon", "baseball", "iloveyou", "trustno1",
            "sunshine", "princess", "football", "shadow", "superman");

    /**
     * Gets the password requirements as a human-readable string.
     *
     * @return the requirements description
     */
    public String getRequirementsDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Password must:\n");
        sb.append("- Be at least ").append(passwordProperties.minLength()).append(" characters long\n");

        if (passwordProperties.requireUppercase()) {
            sb.append("- Contain at least one uppercase letter\n");
        }
        if (passwordProperties.requireLowercase()) {
            sb.append("- Contain at least one lowercase letter\n");
        }
        if (passwordProperties.requireDigit()) {
            sb.append("- Contain at least one digit\n");
        }
        if (passwordProperties.requireSpecial()) {
            sb.append("- Contain at least one special character\n");
        }

        return sb.toString();
    }
}
