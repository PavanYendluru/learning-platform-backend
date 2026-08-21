package com.learningplatform.backend.exception;

public class DomainNotFoundException extends RuntimeException {
    public DomainNotFoundException(Long id) {
        super("Domain not found with id: " + id);
    }
    public DomainNotFoundException(String message) {
        super(message);
    }
}
