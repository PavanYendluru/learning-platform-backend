package com.learningplatform.backend.service;

import com.learningplatform.backend.client.AiProvider;
import com.learningplatform.backend.client.ChatMessagePayload;
import com.learningplatform.backend.client.GeminiClient;
import com.learningplatform.backend.client.GroqClient;
import com.learningplatform.backend.dto.AiStreamEventDto;
import com.learningplatform.backend.exception.AiProviderException;
import com.learningplatform.backend.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Service
public class AiOrchestrationService {

    private static final Logger log = LoggerFactory.getLogger(AiOrchestrationService.class);

    private final GeminiClient geminiClient;
    private final GroqClient groqClient;
    private final boolean fallbackEnabled;

    @Autowired
    public AiOrchestrationService(
            GeminiClient geminiClient,
            GroqClient groqClient,
            @Value("${ai.fallback.enabled:true}") boolean fallbackEnabled) {
        this.geminiClient = geminiClient;
        this.groqClient = groqClient;
        this.fallbackEnabled = fallbackEnabled;
    }

    public boolean isGeminiConfigured() {
        return geminiClient.isConfigured();
    }

    public boolean isGroqConfigured() {
        return groqClient.isConfigured();
    }

    public String getPrimaryModel() {
        return geminiClient.getModelName();
    }

    public String getFallbackModel() {
        return groqClient.getModelName();
    }

    public boolean isFallbackEnabled() {
        return fallbackEnabled;
    }

    /**
     * Synchronous orchestration: tries Gemini first, falls back to Groq if eligible.
     */
    public GenerationResult generateResponse(String systemInstruction, List<ChatMessagePayload> history) {
        // 1. Attempt Primary (Gemini)
        if (geminiClient.isConfigured()) {
            try {
                log.debug("Invoking primary AI provider (Gemini: {})", geminiClient.getModelName());
                String response = geminiClient.generateContent(systemInstruction, history);
                return new GenerationResult(response, geminiClient.getProviderName(), geminiClient.getModelName());
            } catch (AiProviderException e) {
                log.warn("Primary AI provider (Gemini) failed: HTTP {} - {}. Fallback eligible: {}",
                        e.getStatusCode(), e.getMessage(), e.isEligibleForFallback());
                if (!fallbackEnabled || !e.isEligibleForFallback() || !groqClient.isConfigured()) {
                    throw mapToUserFriendlyException(e);
                }
            } catch (Exception e) {
                log.warn("Unexpected failure from Gemini: {}. Attempting Groq fallback.", e.getMessage());
                if (!fallbackEnabled || !groqClient.isConfigured()) {
                    throw new BadRequestException("AI Tutor service is temporarily unavailable. Please try again shortly.");
                }
            }
        } else {
            log.warn("Gemini is not configured. Falling directly to Groq fallback provider.");
            if (!groqClient.isConfigured()) {
                throw new BadRequestException("No AI providers are configured on this server. Please set GEMINI_API_KEY or GROQ_API_KEY.");
            }
        }

        // 2. Attempt Fallback (Groq)
        try {
            log.info("Switching to fallback AI provider (Groq: {})", groqClient.getModelName());
            String response = groqClient.generateContent(systemInstruction, history);
            return new GenerationResult(response, groqClient.getProviderName(), groqClient.getModelName());
        } catch (Exception e) {
            log.error("Fallback AI provider (Groq) also failed: {}", e.getMessage());
            throw new BadRequestException("All AI services are currently unavailable. Please wait a few moments and try again.");
        }
    }

    /**
     * Real-time streaming orchestration:
     * - Streams chunks to SseEmitter.
     * - If Gemini fails before any chunk is sent: sends provider_switch and streams Groq.
     * - If Gemini fails mid-stream: sends reset event, provider_switch event, clears buffer, and streams Groq from start.
     * - Disallows infinite failover loops (max 1 switch: Gemini -> Groq).
     */
    public GenerationResult streamResponse(
            String systemInstruction,
            List<ChatMessagePayload> history,
            SseEmitter emitter,
            AtomicBoolean cancellationToken) {

        StringBuilder accumulatedText = new StringBuilder();
        AtomicInteger chunkCount = new AtomicInteger(0);

        // 1. Try Gemini
        if (geminiClient.isConfigured()) {
            try {
                sendSseEvent(emitter, "start", AiStreamEventDto.start(geminiClient.getProviderName(), geminiClient.getModelName()));

                geminiClient.streamContent(
                        systemInstruction,
                        history,
                        chunk -> {
                            if (cancellationToken.get()) return;
                            accumulatedText.append(chunk);
                            chunkCount.incrementAndGet();
                            sendSseEvent(emitter, "chunk", AiStreamEventDto.chunk(chunk));
                        },
                        cancellationToken
                );

                if (cancellationToken.get()) {
                    log.info("Gemini generation was cancelled by client.");
                    return new GenerationResult(accumulatedText.toString(), geminiClient.getProviderName(), geminiClient.getModelName(), true);
                }

                if (chunkCount.get() > 0 && !accumulatedText.isEmpty()) {
                    return new GenerationResult(accumulatedText.toString(), geminiClient.getProviderName(), geminiClient.getModelName(), false);
                }
                log.warn("Gemini produced empty stream. Evaluating fallback to Groq.");
            } catch (Exception e) {
                log.warn("Gemini streaming error: {}. Evaluating Groq failover. Chunks already emitted: {}",
                        e.getMessage(), chunkCount.get());

                if (!fallbackEnabled || !groqClient.isConfigured() || cancellationToken.get()) {
                    sendSseEvent(emitter, "error", AiStreamEventDto.error(getReadableErrorMessage(e)));
                    return new GenerationResult(accumulatedText.toString(), geminiClient.getProviderName(), geminiClient.getModelName(), true);
                }

                // Mid-stream or pre-stream failover to Groq
                handleFailoverReset(emitter, chunkCount.get(), e.getMessage());
                accumulatedText.setLength(0);
                chunkCount.set(0);
            }
        } else {
            log.warn("Gemini not configured. Directly initiating Groq streaming.");
            if (!groqClient.isConfigured()) {
                sendSseEvent(emitter, "error", AiStreamEventDto.error("No AI provider credentials are configured on this server."));
                return new GenerationResult("", "none", "none", true);
            }
        }

        // 2. Try Groq Fallback
        try {
            sendSseEvent(emitter, "start", AiStreamEventDto.start(groqClient.getProviderName(), groqClient.getModelName()));

            groqClient.streamContent(
                    systemInstruction,
                    history,
                    chunk -> {
                        if (cancellationToken.get()) return;
                        accumulatedText.append(chunk);
                        chunkCount.incrementAndGet();
                        sendSseEvent(emitter, "chunk", AiStreamEventDto.chunk(chunk));
                    },
                    cancellationToken
            );

            if (cancellationToken.get()) {
                log.info("Groq fallback generation was cancelled by client.");
                return new GenerationResult(accumulatedText.toString(), groqClient.getProviderName(), groqClient.getModelName(), true);
            }

            return new GenerationResult(accumulatedText.toString(), groqClient.getProviderName(), groqClient.getModelName(), false);
        } catch (Exception e) {
            log.error("Groq fallback streaming failed: {}", e.getMessage());
            sendSseEvent(emitter, "error", AiStreamEventDto.error("Both AI services are currently unavailable. Please try again shortly."));
            return new GenerationResult(accumulatedText.toString(), groqClient.getProviderName(), groqClient.getModelName(), true);
        }
    }

    private void handleFailoverReset(SseEmitter emitter, int chunksEmitted, String reason) {
        try {
            if (chunksEmitted > 0) {
                // If chunks were already rendered, discard them cleanly so partial text does not merge
                sendSseEvent(emitter, "reset", AiStreamEventDto.reset("Gemini stream interrupted. Switching to Groq fallback."));
            }
            sendSseEvent(emitter, "provider_switch", AiStreamEventDto.providerSwitch(
                    "gemini",
                    "groq",
                    "Gemini unavailable. Automatically continuing with Groq (" + groqClient.getModelName() + ")."
            ));
        } catch (Exception e) {
            log.error("Failed to emit failover reset events to client: {}", e.getMessage());
        }
    }

    private void sendSseEvent(SseEmitter emitter, String eventName, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .name(eventName)
                    .data(data));
        } catch (IOException e) {
            log.debug("Client disconnected while sending SSE event '{}': {}", eventName, e.getMessage());
        }
    }

    private BadRequestException mapToUserFriendlyException(Exception e) {
        if (e instanceof AiProviderException ape) {
            if (ape.getStatusCode() == 429) {
                return new BadRequestException("AI Tutor rate limit or quota exceeded. Please wait a few moments and try again.");
            }
            return new BadRequestException("AI Tutor provider error: " + ape.getMessage());
        }
        return new BadRequestException("AI Tutor service is temporarily unavailable. Please try again shortly.");
    }

    private String getReadableErrorMessage(Exception e) {
        if (e instanceof AiProviderException ape) {
            if (ape.getStatusCode() == 429) {
                return "AI provider quota or rate limit exceeded. Please try again in a few moments.";
            }
            return ape.getMessage();
        }
        return "AI Tutor generation failed. Please try again.";
    }

    public record GenerationResult(String fullText, String provider, String model, boolean cancelled) {
        public GenerationResult(String fullText, String provider, String model) {
            this(fullText, provider, model, false);
        }
    }
}
