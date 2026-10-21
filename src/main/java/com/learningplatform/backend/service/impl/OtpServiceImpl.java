package com.learningplatform.backend.service.impl;

import com.learningplatform.backend.dto.VerifyOtpResponse;
import com.learningplatform.backend.entity.EmailOtp;
import com.learningplatform.backend.enums.OtpPurpose;
import com.learningplatform.backend.exception.BadRequestException;
import com.learningplatform.backend.repository.EmailOtpRepository;
import com.learningplatform.backend.service.OtpService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.UUID;

@Service
@Transactional
public class OtpServiceImpl implements OtpService {

    private final EmailOtpRepository emailOtpRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.otp.expiration-minutes:5}")
    private int expirationMinutes;

    @Value("${app.otp.max-attempts:5}")
    private int maxAttempts;

    @Value("${app.otp.resend-cooldown-seconds:3}")
    private int resendCooldownSeconds;

    @Value("${app.otp.verification-token-expiration-minutes:15}")
    private int tokenExpirationMinutes;

    public OtpServiceImpl(EmailOtpRepository emailOtpRepository) {
        this.emailOtpRepository = emailOtpRepository;
    }

    @Override
    public String generateAndSaveOtp(String email, OtpPurpose purpose) {
        String normalizedEmail = email.trim().toLowerCase();

        // Check resend cooldown
        emailOtpRepository.findTopByEmailIgnoreCaseOrderByCreatedAtDesc(normalizedEmail)
            .ifPresent(lastOtp -> {
                long secondsSinceLast = ChronoUnit.SECONDS.between(lastOtp.getCreatedAt(), LocalDateTime.now());
                if (secondsSinceLast < resendCooldownSeconds) {
                    throw new BadRequestException("Please wait " + (resendCooldownSeconds - secondsSinceLast) + " seconds before requesting a new OTP.");
                }
            });

        // Generate 4-digit cryptographically secure numeric OTP (1000 - 9999)
        int code = 1000 + secureRandom.nextInt(9000);
        String rawOtp = String.valueOf(code);
        String hashedOtp = hash(rawOtp);

        EmailOtp emailOtp = new EmailOtp();
        emailOtp.setEmail(normalizedEmail);
        emailOtp.setOtpHash(hashedOtp);
        emailOtp.setPurpose(purpose);
        emailOtp.setExpiresAt(LocalDateTime.now().plusMinutes(expirationMinutes));
        emailOtp.setMaxAttempts(maxAttempts);
        emailOtp.setAttemptCount(0);
        emailOtp.setVerified(false);
        emailOtp.setConsumed(false);

        emailOtpRepository.save(emailOtp);

        return rawOtp;
    }

    @Override
    public VerifyOtpResponse verifyOtp(String email, String rawOtp, OtpPurpose purpose) {
        String normalizedEmail = email.trim().toLowerCase();

        EmailOtp emailOtp = emailOtpRepository.findTopByEmailIgnoreCaseAndPurposeOrderByIdDesc(normalizedEmail, purpose)
                .orElseThrow(() -> new BadRequestException("No verification request found for this email."));

        if (emailOtp.isConsumed()) {
            throw new BadRequestException("This verification code has already been used.");
        }

        if (emailOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Verification code has expired. Please request a new one.");
        }

        if (emailOtp.getAttemptCount() >= emailOtp.getMaxAttempts()) {
            throw new BadRequestException("Maximum verification attempts exceeded. Please request a new code.");
        }

        String inputHash = hash(rawOtp.trim());
        if (!inputHash.equals(emailOtp.getOtpHash())) {
            emailOtp.setAttemptCount(emailOtp.getAttemptCount() + 1);
            emailOtpRepository.save(emailOtp);
            int remaining = emailOtp.getMaxAttempts() - emailOtp.getAttemptCount();
            throw new BadRequestException("Invalid verification code. Attempts remaining: " + Math.max(0, remaining));
        }

        // Verified successfully! Generate secure random token
        String verificationToken = UUID.randomUUID().toString();
        emailOtp.setVerified(true);
        emailOtp.setVerificationToken(verificationToken);
        // Extend validity for password creation phase
        emailOtp.setExpiresAt(LocalDateTime.now().plusMinutes(tokenExpirationMinutes));
        emailOtpRepository.save(emailOtp);

        return new VerifyOtpResponse(verificationToken, tokenExpirationMinutes * 60L, "Code verified successfully");
    }

    @Override
    public boolean validateAndConsumeToken(String email, String verificationToken, OtpPurpose purpose) {
        String normalizedEmail = email.trim().toLowerCase();

        EmailOtp emailOtp = emailOtpRepository.findByEmailIgnoreCaseAndVerificationTokenAndPurposeAndIsVerifiedTrueAndIsConsumedFalse(
                normalizedEmail, verificationToken.trim(), purpose)
                .orElse(null);

        if (emailOtp == null) {
            return false;
        }

        if (emailOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }

        emailOtp.setConsumed(true);
        emailOtpRepository.save(emailOtp);
        return true;
    }

    private String hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
