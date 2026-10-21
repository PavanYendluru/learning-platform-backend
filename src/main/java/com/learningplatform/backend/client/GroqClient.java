package com.learningplatform.backend.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learningplatform.backend.exception.AiProviderException;
import com.learningplatform.backend.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Component
public class GroqClient implements AiProvider {

    private static final Logger log = LoggerFactory.getLogger(GroqClient.class);
    public static final String DEFAULT_GROQ_MODEL = "openai/gpt-oss-20b";

    private final String apiKey;
    private final String baseUrl;
    private final String model;
    private final int timeoutSeconds;
    private final int maxTokens;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    @Autowired
    public GroqClient(
            @Value("${groq.api.key:}") String apiKey,
            @Value("${groq.api.url:https://api.groq.com/openai/v1}") String baseUrl,
            @Value("${groq.model:openai/gpt-oss-20b}") String model,
            @Value("${groq.timeout-seconds:60}") int timeoutSeconds,
            @Value("${groq.max-tokens:2048}") int maxTokens,
            ObjectMapper objectMapper) {

        this.apiKey = apiKey != null ? apiKey.trim() : "";
        this.baseUrl = baseUrl != null && !baseUrl.isBlank() ? baseUrl.trim() : "https://api.groq.com/openai/v1";
        this.model = model != null && !model.isBlank() ? model.trim() : DEFAULT_GROQ_MODEL;
        this.timeoutSeconds = timeoutSeconds > 0 ? timeoutSeconds : 60;
        this.maxTokens = maxTokens > 0 ? maxTokens : 2048;
        this.objectMapper = objectMapper;

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();

        if (isConfigured()) {
            log.info("Groq AI Provider initialized with model: {}", this.model);
        } else {
            log.warn("Groq API key is not configured. Fallback to Groq will not be available until GROQ_API_KEY is set.");
        }
    }

    @Override
    public String getProviderName() {
        return "groq";
    }

    @Override
    public String getModelName() {
        return model;
    }

    @Override
    public boolean isConfigured() {
        return !apiKey.isBlank();
    }

    @Override
    public String generateContent(String systemInstruction, List<ChatMessagePayload> history) {
        if (!isConfigured()) {
            throw new AiProviderException("groq", 401, "Groq API key is not configured. Set GROQ_API_KEY.", false);
        }

        StringBuilder fullResponse = new StringBuilder();
        try {
            streamContent(systemInstruction, history, fullResponse::append, new AtomicBoolean(false));
            return fullResponse.toString().trim();
        } catch (AiProviderException e) {
            throw e;
        } catch (Exception e) {
            throw new AiProviderException("groq", 500, "Groq generation failed: " + e.getMessage(), false, e);
        }
    }

    @Override
    public void streamContent(String systemInstruction,
                              List<ChatMessagePayload> history,
                              Consumer<String> chunkConsumer,
                              AtomicBoolean cancellationToken) throws Exception {

        if (!isConfigured()) {
            throw new AiProviderException("groq", 401, "Groq API key is not configured. Set GROQ_API_KEY.", false);
        }

        List<GroqMessage> messages = new ArrayList<>();
        if (systemInstruction != null && !systemInstruction.isBlank()) {
            messages.add(new GroqMessage("system", systemInstruction));
        }

        if (history != null) {
            for (ChatMessagePayload payload : history) {
                String role = "user".equalsIgnoreCase(payload.role()) ? "user" : "assistant";
                messages.add(new GroqMessage(role, payload.text()));
            }
        }

        GroqChatRequest requestPayload = new GroqChatRequest(
                model,
                messages,
                0.7,
                maxTokens,
                true
        );

        String jsonBody = objectMapper.writeValueAsString(requestPayload);
        String endpointUrl = baseUrl.endsWith("/") ? baseUrl + "chat/completions" : baseUrl + "/chat/completions";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpointUrl))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .header("Accept", "text/event-stream")
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                .build();

        HttpResponse<InputStream> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
        } catch (Exception e) {
            log.error("Network error communicating with Groq API: {}", e.getMessage());
            throw new AiProviderException("groq", 503, "Failed to connect to Groq AI service: " + e.getMessage(), true, e);
        }

        int statusCode = response.statusCode();
        if (statusCode >= 400) {
            String errorBody = "";
            try (InputStream is = response.body()) {
                errorBody = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            } catch (Exception ignored) {}

            log.error("Groq API returned error status {}: {}", statusCode, errorBody);
            boolean eligible = statusCode == 429 || statusCode >= 500;
            throw new AiProviderException("groq", statusCode, "Groq API error (" + statusCode + "): " + errorBody, eligible);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (cancellationToken != null && cancellationToken.get()) {
                    log.info("Groq stream cancelled by client");
                    break;
                }

                line = line.trim();
                if (line.isEmpty() || line.startsWith(":")) {
                    continue;
                }

                if (line.startsWith("data:")) {
                    String data = line.substring(5).trim();
                    if ("[DONE]".equals(data)) {
                        break;
                    }

                    try {
                        GroqStreamChunk chunk = objectMapper.readValue(data, GroqStreamChunk.class);
                        if (chunk != null && chunk.choices != null && !chunk.choices.isEmpty()) {
                            GroqStreamChoice choice = chunk.choices.get(0);
                            if (choice.delta != null && choice.delta.content != null && !choice.delta.content.isEmpty()) {
                                chunkConsumer.accept(choice.delta.content);
                            }
                        }
                    } catch (Exception e) {
                        log.debug("Skipping unparseable Groq SSE line: {}", data);
                    }
                }
            }
        }
    }

    // DTO records for Groq API
    public record GroqChatRequest(
            String model,
            List<GroqMessage> messages,
            double temperature,
            @JsonProperty("max_tokens") int maxTokens,
            boolean stream
    ) {}

    public record GroqMessage(String role, String content) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GroqStreamChunk {
        public List<GroqStreamChoice> choices;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GroqStreamChoice {
        public GroqStreamDelta delta;
        @JsonProperty("finish_reason")
        public String finishReason;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GroqStreamDelta {
        public String role;
        public String content;
    }
}
