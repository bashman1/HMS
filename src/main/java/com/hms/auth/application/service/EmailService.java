package com.hms.auth.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Service for sending emails.
 * 
 * <p>
 * Handles email verification and password reset emails.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.verification.base-url}")
    private String verificationBaseUrl;

    @Value("${app.email.password-reset.base-url}")
    private String passwordResetBaseUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends a verification email asynchronously.
     *
     * @param to    recipient email
     * @param name  recipient name
     * @param token verification token
     */
    @Async
    public void sendVerificationEmail(String to, String name, String token) {
        log.info("Sending verification email to: {}", to);

        String verificationUrl = verificationBaseUrl + "?token=" + token;
        String subject = "Verify Your Email - HMS";
        String content = buildVerificationEmailContent(name, verificationUrl);

        sendEmail(to, subject, content);
    }

    /**
     * Sends a password reset email asynchronously.
     *
     * @param to    recipient email
     * @param name  recipient name
     * @param token reset token
     */
    @Async
    public void sendPasswordResetEmail(String to, String name, String token) {
        log.info("Sending password reset email to: {}", to);

        String resetUrl = passwordResetBaseUrl + "?token=" + token;
        String subject = "Reset Your Password - HMS";
        String content = buildPasswordResetEmailContent(name, resetUrl);

        sendEmail(to, subject, content);
    }

    /**
     * Sends an email.
     */
    private void sendEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", to, e);
        }
    }

    /**
     * Builds verification email HTML content.
     */
    private String buildVerificationEmailContent(String name, String verificationUrl) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #007bff; color: white; padding: 20px; text-align: center; }
                        .content { padding: 20px; background-color: #f9f9f9; }
                        .button { display: inline-block; padding: 12px 24px; background-color: #007bff;
                                  color: white; text-decoration: none; border-radius: 4px; margin: 20px 0; }
                        .footer { padding: 20px; text-align: center; font-size: 12px; color: #666; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>HMS Authentication</h1>
                        </div>
                        <div class="content">
                            <h2>Hello %s,</h2>
                            <p>Thank you for registering with HMS. Please verify your email address by clicking the button below:</p>
                            <p style="text-align: center;">
                                <a href="%s" class="button">Verify Email</a>
                            </p>
                            <p>Or copy and paste this link into your browser:</p>
                            <p style="word-break: break-all;">%s</p>
                            <p>This link will expire in 24 hours.</p>
                            <p>If you didn't create an account, please ignore this email.</p>
                        </div>
                        <div class="footer">
                            <p>&copy; 2024 HMS. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(name, verificationUrl, verificationUrl);
    }

    /**
     * Builds password reset email HTML content.
     */
    private String buildPasswordResetEmailContent(String name, String resetUrl) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #dc3545; color: white; padding: 20px; text-align: center; }
                        .content { padding: 20px; background-color: #f9f9f9; }
                        .button { display: inline-block; padding: 12px 24px; background-color: #dc3545;
                                  color: white; text-decoration: none; border-radius: 4px; margin: 20px 0; }
                        .footer { padding: 20px; text-align: center; font-size: 12px; color: #666; }
                        .warning { background-color: #fff3cd; padding: 10px; border-radius: 4px; margin: 10px 0; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Password Reset</h1>
                        </div>
                        <div class="content">
                            <h2>Hello %s,</h2>
                            <p>We received a request to reset your password. Click the button below to create a new password:</p>
                            <p style="text-align: center;">
                                <a href="%s" class="button">Reset Password</a>
                            </p>
                            <p>Or copy and paste this link into your browser:</p>
                            <p style="word-break: break-all;">%s</p>
                            <div class="warning">
                                <strong>⚠️ Security Notice:</strong> This link will expire in 1 hour.
                                If you didn't request a password reset, please ignore this email and your password will remain unchanged.
                            </div>
                        </div>
                        <div class="footer">
                            <p>&copy; 2024 HMS. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(name, resetUrl, resetUrl);
    }
}
