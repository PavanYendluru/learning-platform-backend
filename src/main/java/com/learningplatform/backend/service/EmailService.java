package com.learningplatform.backend.service;

public interface EmailService {
    void sendRegistrationOtp(String toEmail, String name, String otp, int validityMinutes);
    void sendPasswordResetOtp(String toEmail, String otp, int validityMinutes);
}
