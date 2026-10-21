package com.learningplatform.backend.service.impl;

import com.learningplatform.backend.dto.*;
import com.learningplatform.backend.entity.User;
import com.learningplatform.backend.enums.OtpPurpose;
import com.learningplatform.backend.enums.Role;
import com.learningplatform.backend.exception.BadRequestException;
import com.learningplatform.backend.exception.ConflictException;
import com.learningplatform.backend.repository.UserRepository;
import com.learningplatform.backend.security.JwtService;
import com.learningplatform.backend.service.AuthService;
import com.learningplatform.backend.service.EmailService;
import com.learningplatform.backend.service.OtpService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final OtpService otpService;
    private final EmailService emailService;

    @Value("${app.otp.expiration-minutes:5}")
    private int expirationMinutes;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           OtpService otpService,
                           EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.otpService = otpService;
        this.emailService = emailService;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("Email is already registered");
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER); // Registration always creates USER

        User saved = userRepository.save(user);

        String token = jwtService.generateToken(saved.getEmail(), saved.getRole().name());

        return new AuthResponse(token, saved.getId(), saved.getName(),
                saved.getEmail(), saved.getRole().name());
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail() != null ? request.getEmail().trim() : "";
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!user.isEnabled()) {
            throw new BadRequestException("Account is deactivated. Please contact support.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

        return new AuthResponse(token, user.getId(), user.getName(),
                user.getEmail(), user.getRole().name());
    }

    @Override
    public void sendRegistrationOtp(SendOtpRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("This email address is already registered. Please log in.");
        }

        String rawOtp = otpService.generateAndSaveOtp(email, OtpPurpose.REGISTRATION);
        emailService.sendRegistrationOtp(email, request.getName(), rawOtp, expirationMinutes);
    }

    @Override
    public VerifyOtpResponse verifyRegistrationOtp(VerifyOtpRequest request) {
        return otpService.verifyOtp(request.getEmail(), request.getOtp(), OtpPurpose.REGISTRATION);
    }

    @Override
    public AuthResponse completeRegistration(CompleteRegistrationRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("Email is already registered");
        }

        boolean validToken = otpService.validateAndConsumeToken(
                email, request.getVerificationToken(), OtpPurpose.REGISTRATION);

        if (!validToken) {
            throw new BadRequestException("Invalid or expired verification session. Please verify your email again.");
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setEnabled(true);

        User saved = userRepository.save(user);
        String token = jwtService.generateToken(saved.getEmail(), saved.getRole().name());

        return new AuthResponse(token, saved.getId(), saved.getName(),
                saved.getEmail(), saved.getRole().name());
    }

    @Override
    public void sendPasswordResetOtp(SendOtpRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        // Check if user exists. To prevent user enumeration, we still return success even if user not found,
        // but only generate and send OTP if user genuinely exists in DB.
        userRepository.findByEmailIgnoreCase(email).ifPresent(user -> {
            String rawOtp = otpService.generateAndSaveOtp(email, OtpPurpose.PASSWORD_RESET);
            emailService.sendPasswordResetOtp(email, rawOtp, expirationMinutes);
        });
    }

    @Override
    public VerifyOtpResponse verifyPasswordResetOtp(VerifyOtpRequest request) {
        return otpService.verifyOtp(request.getEmail(), request.getOtp(), OtpPurpose.PASSWORD_RESET);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        boolean validToken = otpService.validateAndConsumeToken(
                email, request.getResetToken(), OtpPurpose.PASSWORD_RESET);

        if (!validToken) {
            throw new BadRequestException("Invalid or expired reset session. Please request a new password reset.");
        }

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadRequestException("User account not found."));

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
