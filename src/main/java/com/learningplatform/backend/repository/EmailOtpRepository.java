package com.learningplatform.backend.repository;

import com.learningplatform.backend.entity.EmailOtp;
import com.learningplatform.backend.enums.OtpPurpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailOtpRepository extends JpaRepository<EmailOtp, Long> {

    Optional<EmailOtp> findTopByEmailIgnoreCaseAndPurposeOrderByIdDesc(String email, OtpPurpose purpose);

    Optional<EmailOtp> findByEmailIgnoreCaseAndVerificationTokenAndPurposeAndIsVerifiedTrueAndIsConsumedFalse(
            String email, String verificationToken, OtpPurpose purpose);

    Optional<EmailOtp> findTopByEmailIgnoreCaseOrderByCreatedAtDesc(String email);
}
