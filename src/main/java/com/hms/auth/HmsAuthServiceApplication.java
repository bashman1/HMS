package com.hms.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * HMS Authentication Service Application.
 * 
 * <p>A production-ready Spring Boot 3.2+ microservice providing comprehensive
 * authentication and authorization capabilities including:
 * <ul>
 *   <li>JWT-based authentication with access and refresh tokens</li>
 *   <li>User registration with email verification</li>
 *   <li>Login with rate limiting and brute force protection</li>
 *   <li>Password reset functionality</li>
 *   <li>Role-based access control (RBAC)</li>
 * </ul>
 *
 * <p>This application leverages Java 21 features including virtual threads,
 * pattern matching, and record patterns for improved performance and code clarity.
 *
 * @author HMS Team
 * @version 1.0.0
 */
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableCaching
@EnableAsync
@EnableScheduling
public class HmsAuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HmsAuthServiceApplication.class, args);
    }
}
