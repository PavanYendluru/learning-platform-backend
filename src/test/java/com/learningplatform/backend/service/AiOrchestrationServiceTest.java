package com.learningplatform.backend.service;

import com.learningplatform.backend.client.ChatMessagePayload;
import com.learningplatform.backend.client.GeminiClient;
import com.learningplatform.backend.client.GroqClient;
import com.learningplatform.backend.exception.AiProviderException;
import com.learningplatform.backend.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiOrchestrationServiceTest {

    @Mock
    private GeminiClient geminiClient;

    @Mock
    private GroqClient groqClient;

    private AiOrchestrationService orchestrationService;

    @BeforeEach
    void setUp() {
        orchestrationService = new AiOrchestrationService(geminiClient, groqClient, true);
    }

    @Test
    void testGenerateResponse_GeminiSucceeds_GroqNotCalled() {
        when(geminiClient.isConfigured()).thenReturn(true);
        when(geminiClient.getModelName()).thenReturn("gemini-3.1-flash-lite");
        when(geminiClient.getProviderName()).thenReturn("gemini");
        when(geminiClient.generateContent(anyString(), anyList())).thenReturn("Gemini answer");

        AiOrchestrationService.GenerationResult result = orchestrationService.generateResponse(
                "You are a tutor", List.of(new ChatMessagePayload("user", "Hello"))
        );

        assertNotNull(result);
        assertEquals("Gemini answer", result.fullText());
        assertEquals("gemini", result.provider());
        assertEquals("gemini-3.1-flash-lite", result.model());
        verify(groqClient, never()).generateContent(anyString(), anyList());
    }

    @Test
    void testGenerateResponse_Gemini429_TriggersGroqFallback() {
        when(geminiClient.isConfigured()).thenReturn(true);
        when(geminiClient.getModelName()).thenReturn("gemini-3.1-flash-lite");
        when(geminiClient.generateContent(anyString(), anyList()))
                .thenThrow(new AiProviderException("gemini", 429, "Rate limit exceeded", true));

        when(groqClient.isConfigured()).thenReturn(true);
        when(groqClient.getModelName()).thenReturn("openai/gpt-oss-20b");
        when(groqClient.getProviderName()).thenReturn("groq");
        when(groqClient.generateContent(anyString(), anyList())).thenReturn("Groq fallback answer");

        AiOrchestrationService.GenerationResult result = orchestrationService.generateResponse(
                "You are a tutor", List.of(new ChatMessagePayload("user", "Hello"))
        );

        assertNotNull(result);
        assertEquals("Groq fallback answer", result.fullText());
        assertEquals("groq", result.provider());
        assertEquals("openai/gpt-oss-20b", result.model());
        verify(geminiClient).generateContent(anyString(), anyList());
        verify(groqClient).generateContent(anyString(), anyList());
    }

    @Test
    void testGenerateResponse_BothProvidersFail_ThrowsBadRequestException() {
        when(geminiClient.isConfigured()).thenReturn(true);
        when(geminiClient.getModelName()).thenReturn("gemini-3.1-flash-lite");
        when(geminiClient.generateContent(anyString(), anyList()))
                .thenThrow(new AiProviderException("gemini", 503, "Service unavailable", true));

        when(groqClient.isConfigured()).thenReturn(true);
        when(groqClient.getModelName()).thenReturn("openai/gpt-oss-20b");
        when(groqClient.generateContent(anyString(), anyList()))
                .thenThrow(new RuntimeException("Groq also down"));

        assertThrows(BadRequestException.class, () -> orchestrationService.generateResponse(
                "You are a tutor", List.of(new ChatMessagePayload("user", "Hello"))
        ));
    }
}
