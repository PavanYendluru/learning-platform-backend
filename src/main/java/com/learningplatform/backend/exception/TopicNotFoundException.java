package com.learningplatform.backend.exception;

public class TopicNotFoundException extends RuntimeException {
    public TopicNotFoundException(Long id) {
        super("Topic not found with id: " + id);
    }
}