package com.hms.auth.infrastructure.web.exception;

import com.hms.auth.domain.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler providing RFC 7807 Problem Details responses.
 * 
 * <p>
 * This handler converts all exceptions to standardized Problem Details
 * format as specified in RFC 7807.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String PROBLEM_BASE_URI = "https://api.hms.com/problems/";

    /**
     * Handles AuthException and its subclasses.
     */
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ProblemDetail> handleAuthException(
            AuthException ex, HttpServletRequest request) {

        log.warn("Authentication error: {} - {}", ex.getErrorCode(), ex.getMessage());

        ProblemDetail problem = createProblemDetail(
                ex.getStatus(),
                ex.getMessage(),
                ex.getErrorCode(),
                request.getRequestURI());

        // Add specific properties for certain exceptions
        if (ex instanceof AccountLockedException lockedException) {
            if (lockedException.getUnlockTime() != null) {
                problem.setProperty("unlockTime", lockedException.getUnlockTime().toString());
            }
        } else if (ex instanceof RateLimitExceededException rateLimitException) {
            problem.setProperty("retryAfter", rateLimitException.getRetryAfterSeconds());
        } else if (ex instanceof PasswordValidationException passwordException) {
            problem.setProperty("violations", passwordException.getViolations());
        }

        return ResponseEntity.status(ex.getStatus())
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    /**
     * Handles Spring Security authentication exceptions.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ProblemDetail> handleAuthenticationException(
            AuthenticationException ex, HttpServletRequest request) {

        log.warn("Authentication failed: {}", ex.getMessage());

        ProblemDetail problem = createProblemDetail(
                HttpStatus.UNAUTHORIZED,
                "Authentication failed",
                "AUTHENTICATION_FAILED",
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    /**
     * Handles access denied exceptions.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {

        log.warn("Access denied: {}", ex.getMessage());

        ProblemDetail problem = createProblemDetail(
                HttpStatus.FORBIDDEN,
                "Access denied. You don't have permission to access this resource.",
                "ACCESS_DENIED",
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    /**
     * Handles constraint violation exceptions.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {

        Map<String, String> violations = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage,
                        (existing, replacement) -> existing));

        ProblemDetail problem = createProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                "VALIDATION_ERROR",
                request.getRequestURI());
        problem.setProperty("violations", violations);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    /**
     * Handles method argument validation exceptions.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = error instanceof FieldError fieldError
                    ? fieldError.getField()
                    : error.getObjectName();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Validation failed for one or more fields");
        problem.setType(URI.create(PROBLEM_BASE_URI + "validation-error"));
        problem.setTitle("Validation Error");
        problem.setProperty("errorCode", "VALIDATION_ERROR");
        problem.setProperty("violations", errors);
        problem.setProperty("timestamp", Instant.now().toString());
        problem.setProperty("correlationId", MDC.get("correlationId"));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    /**
     * Handles all other exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleAllExceptions(
            Exception ex, HttpServletRequest request) {

        log.error("Unexpected error occurred", ex);

        ProblemDetail problem = createProblemDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again later.",
                "INTERNAL_ERROR",
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    /**
     * Creates a ProblemDetail with standard properties.
     */
    private ProblemDetail createProblemDetail(
            HttpStatus status,
            String detail,
            String errorCode,
            String instance) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setType(URI.create(PROBLEM_BASE_URI + errorCode.toLowerCase().replace("_", "-")));
        problem.setTitle(formatTitle(errorCode));
        problem.setInstance(URI.create(instance));
        problem.setProperty("errorCode", errorCode);
        problem.setProperty("timestamp", Instant.now().toString());
        problem.setProperty("correlationId", MDC.get("correlationId"));

        return problem;
    }

    /**
     * Formats an error code into a human-readable title.
     */
    private String formatTitle(String errorCode) {
        return errorCode.replace("_", " ")
                .toLowerCase()
                .substring(0, 1).toUpperCase() +
                errorCode.replace("_", " ").toLowerCase().substring(1);
    }
}
