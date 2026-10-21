package com.learningplatform.backend.exception;

public class AiProviderException extends RuntimeException {

    private final String provider;
    private final int statusCode;
    private final boolean eligibleForFallback;

    public AiProviderException(String provider, int statusCode, String message, boolean eligibleForFallback) {
        super(message);
        this.provider = provider;
        this.statusCode = statusCode;
        this.eligibleForFallback = eligibleForFallback;
    }

    public AiProviderException(String provider, int statusCode, String message, boolean eligibleForFallback, Throwable cause) {
        super(message, cause);
        this.provider = provider;
        this.statusCode = statusCode;
        this.eligibleForFallback = eligibleForFallback;
    }

    public String getProvider() {
        return provider;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public boolean isEligibleForFallback() {
        return eligibleForFallback;
    }
}
