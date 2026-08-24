package com.learningplatform.backend.dto;

import com.learningplatform.backend.enums.ResourceVerificationStatus;
import jakarta.validation.constraints.NotNull;

public class ResourceVerificationRequest {

    @NotNull(message = "Verification status is required")
    private ResourceVerificationStatus verificationStatus;

    public ResourceVerificationStatus getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(ResourceVerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }
}