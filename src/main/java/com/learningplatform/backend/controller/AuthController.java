package com.learningplatform.backend.controller;

import com.learningplatform.backend.dto.*;
import com.learningplatform.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    /* ==============================================================
       REGISTRATION FLOW WITH EMAIL OTP
       ============================================================== */

    @PostMapping("/register/send-otp")
    public ResponseEntity<ApiResponse<Void>> sendRegistrationOtp(
            @Valid @RequestBody SendOtpRequest request) {
        authService.sendRegistrationOtp(request);
        return ResponseEntity.ok(ApiResponse.success(
                "Verification code has been sent to your email address.", null));
    }

    @PostMapping("/register/verify-otp")
    public ResponseEntity<ApiResponse<VerifyOtpResponse>> verifyRegistrationOtp(
            @Valid @RequestBody VerifyOtpRequest request) {
        VerifyOtpResponse response = authService.verifyRegistrationOtp(request);
        return ResponseEntity.ok(ApiResponse.success("Email verified successfully.", response));
    }

    @PostMapping("/register/complete")
    public ResponseEntity<ApiResponse<AuthResponse>> completeRegistration(
            @Valid @RequestBody CompleteRegistrationRequest request) {
        AuthResponse response = authService.completeRegistration(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Account created successfully. You can now log in.", response));
    }

    // Keep original /register endpoint as legacy compatibility
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful", response));
    }

    /* ==============================================================
       FORGOT PASSWORD FLOW WITH EMAIL OTP
       ============================================================== */

    @PostMapping("/forgot-password/send-otp")
    public ResponseEntity<ApiResponse<Void>> sendPasswordResetOtp(
            @Valid @RequestBody SendOtpRequest request) {
        authService.sendPasswordResetOtp(request);
        return ResponseEntity.ok(ApiResponse.success(
                "If an account exists with this email address, a 4-digit verification code has been sent.", null));
    }

    @PostMapping("/forgot-password/verify-otp")
    public ResponseEntity<ApiResponse<VerifyOtpResponse>> verifyPasswordResetOtp(
            @Valid @RequestBody VerifyOtpRequest request) {
        VerifyOtpResponse response = authService.verifyPasswordResetOtp(request);
        return ResponseEntity.ok(ApiResponse.success("Verification code confirmed.", response));
    }

    @PostMapping("/forgot-password/reset")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password has been reset successfully. Please log in with your new password.", null));
    }
}
