package com.learningplatform.backend.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learningplatform.backend.exception.AiProviderException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GroqClientTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testIsConfigured_WhenApiKeyIsEmpty_ReturnsFalse() {
        GroqClient client = new GroqClient("", "https://api.groq.com/openai/v1", "openai/gpt-oss-20b", 60, 2048, objectMapper);
        assertFalse(client.isConfigured());
        assertEquals("groq", client.getProviderName());
        assertEquals("openai/gpt-oss-20b", client.getModelName());
    }

    @Test
    void testIsConfigured_WhenApiKeyIsPresent_ReturnsTrue() {
        GroqClient client = new GroqClient("gsk-mock-key-12345", "https://api.groq.com/openai/v1", "openai/gpt-oss-20b", 60, 2048, objectMapper);
        assertTrue(client.isConfigured());
        assertEquals("openai/gpt-oss-20b", client.getModelName());
    }

    @Test
    void testGenerateContent_WhenNotConfigured_ThrowsAiProviderException() {
        GroqClient client = new GroqClient("", "https://api.groq.com/openai/v1", "openai/gpt-oss-20b", 60, 2048, objectMapper);
        AiProviderException ex = assertThrows(
                AiProviderException.class,
                () -> client.generateContent("You are a tutor", List.of(new ChatMessagePayload("user", "Hello")))
        );
        assertEquals("groq", ex.getProvider());
    }
}
