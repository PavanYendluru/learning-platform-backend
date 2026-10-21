package com.learningplatform.backend.service;

import com.learningplatform.backend.dto.VerifyOtpResponse;
import com.learningplatform.backend.enums.OtpPurpose;

public interface OtpService {
    String generateAndSaveOtp(String email, OtpPurpose purpose);
    VerifyOtpResponse verifyOtp(String email, String otp, OtpPurpose purpose);
    boolean validateAndConsumeToken(String email, String verificationToken, OtpPurpose purpose);
}
