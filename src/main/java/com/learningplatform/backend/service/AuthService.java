package com.learningplatform.backend.service;

import com.learningplatform.backend.dto.*;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);

    void sendRegistrationOtp(SendOtpRequest request);
    VerifyOtpResponse verifyRegistrationOtp(VerifyOtpRequest request);
    AuthResponse completeRegistration(CompleteRegistrationRequest request);

    void sendPasswordResetOtp(SendOtpRequest request);
    VerifyOtpResponse verifyPasswordResetOtp(VerifyOtpRequest request);
    void resetPassword(ResetPasswordRequest request);
}
