package com.learningplatform.backend.client;

import com.learningplatform.backend.exception.AiProviderException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GeminiClientTest {

    @Test
    void testIsConfigured_WhenApiKeyIsEmpty_ReturnsFalse() {
        GeminiClient client = new GeminiClient("", "https://example.com", "gemini-3.1-flash-lite", 30, 2048);
        assertFalse(client.isConfigured());
        assertEquals("gemini", client.getProviderName());
        assertEquals("gemini-3.1-flash-lite", client.getModel());
    }

    @Test
    void testEnforcesGemini31FlashLite_EvenIfOtherModelPassed() {
        GeminiClient client = new GeminiClient("mock-test-api-key", "https://example.com", "gemini-3.6-flash", 30, 2048);
        assertTrue(client.isConfigured());
        assertEquals("gemini-3.1-flash-lite", client.getModel());
        assertEquals("gemini-3.1-flash-lite", client.getModelName());
    }

    @Test
    void testGenerateContent_WhenNotConfigured_ThrowsAiProviderException() {
        GeminiClient client = new GeminiClient("", "https://example.com", "gemini-3.1-flash-lite", 30, 2048);
        AiProviderException ex = assertThrows(
                AiProviderException.class,
                () -> client.generateContent("You are a tutor", List.of(new ChatMessagePayload("user", "Hello")))
        );
        assertEquals("gemini", ex.getProvider());
        assertTrue(ex.isEligibleForFallback());
    }
}
