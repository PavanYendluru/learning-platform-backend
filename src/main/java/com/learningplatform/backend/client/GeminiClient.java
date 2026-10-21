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
public class GeminiClient implements AiProvider {

    private static final Logger log = LoggerFactory.getLogger(GeminiClient.class);

    /**
     * Enforce gemini-3.1-flash-lite as the only permitted Gemini model.
     * Silent substitution or environment variable overrides are strictly disallowed.
     */
    public static final String ENFORCED_MODEL = "gemini-3.1-flash-lite";

    private final String apiKey;
    private final String baseUrl;
    private final int timeoutSeconds;
    private final int maxTokens;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    @Autowired
    public GeminiClient(
            @Value("${gemini.api.key:}") String apiKey,
            @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models}") String baseUrl,
            @Value("${gemini.model:gemini-3.1-flash-lite}") String ignoredConfiguredModel,
            @Value("${gemini.timeout-seconds:60}") int timeoutSeconds,
            @Value("${gemini.max-tokens:2048}") int maxTokens,
            ObjectMapper objectMapper) {

        this.apiKey = apiKey != null ? apiKey.trim() : "";
        this.baseUrl = baseUrl != null && !baseUrl.isBlank() ? baseUrl.trim() : "https://generativelanguage.googleapis.com/v1beta/models";
        this.timeoutSeconds = timeoutSeconds > 0 ? timeoutSeconds : 60;
        this.maxTokens = maxTokens > 0 ? maxTokens : 2048;
        this.objectMapper = objectMapper;

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();

        if (ignoredConfiguredModel != null && !ignoredConfiguredModel.isBlank() && !ENFORCED_MODEL.equals(ignoredConfiguredModel)) {
            log.warn("Gemini model '{}' was requested via configuration, but '{}' is strictly enforced as the primary model.",
                    ignoredConfiguredModel, ENFORCED_MODEL);
        }

        if (isConfigured()) {
            log.info("Gemini AI Provider initialized with enforced model: {}", ENFORCED_MODEL);
        } else {
            log.warn("Gemini API key is not configured. AI Tutor will rely on fallback provider if available.");
        }
    }

    // Overload constructor for testing
    public GeminiClient(String apiKey, String baseUrl, String ignoredModel, int timeoutSeconds, int maxTokens) {
        this(apiKey, baseUrl, ignoredModel, timeoutSeconds, maxTokens, new ObjectMapper());
    }

    @Override
    public String getProviderName() {
        return "gemini";
    }

    @Override
    public String getModelName() {
        return ENFORCED_MODEL;
    }

    public String getModel() {
        return ENFORCED_MODEL;
    }

    @Override
    public boolean isConfigured() {
        return !apiKey.isBlank();
    }

    @Override
    public String generateContent(String systemInstruction, List<ChatMessagePayload> history) {
        if (!isConfigured()) {
            throw new AiProviderException("gemini", 401,
                    "Gemini API key is not configured. Please set the GEMINI_API_KEY environment variable on your server.", true);
        }

        StringBuilder fullResponse = new StringBuilder();
        try {
            streamContent(systemInstruction, history, fullResponse::append, new AtomicBoolean(false));
            return fullResponse.toString().trim();
        } catch (AiProviderException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to communicate with Gemini API: {}", e.getMessage());
            throw new AiProviderException("gemini", 500, "Failed to reach Gemini AI service: " + e.getMessage(), true, e);
        }
    }

    @Override
    public void streamContent(String systemInstruction,
                              List<ChatMessagePayload> history,
                              Consumer<String> chunkConsumer,
                              AtomicBoolean cancellationToken) throws Exception {

        if (!isConfigured()) {
            throw new AiProviderException("gemini", 401,
                    "Gemini API key is not configured. Please set the GEMINI_API_KEY environment variable.", true);
        }

        String endpointUrl = String.format("%s/%s:streamGenerateContent?alt=sse", baseUrl, ENFORCED_MODEL);

        GeminiRequest requestPayload = buildRequestPayload(systemInstruction, history);
        String jsonBody = objectMapper.writeValueAsString(requestPayload);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpointUrl))
                .header("x-goog-api-key", apiKey)
                .header("Content-Type", "application/json")
                .header("Accept", "text/event-stream")
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                .build();

        HttpResponse<InputStream> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
        } catch (Exception e) {
            log.error("Network error communicating with Gemini API: {}", e.getMessage());
            throw new AiProviderException("gemini", 503, "Network failure connecting to Gemini AI: " + e.getMessage(), true, e);
        }

        int statusCode = response.statusCode();
        if (statusCode >= 400) {
            String errorBody = "";
            try (InputStream is = response.body()) {
                errorBody = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            } catch (Exception ignored) {}

            log.error("Gemini API error (HTTP {}): {}", statusCode, errorBody);

            if (statusCode == 429) {
                throw new AiProviderException("gemini", 429,
                        "Gemini rate limit or quota exceeded. Failing over to Groq.", true);
            } else if (statusCode >= 500) {
                throw new AiProviderException("gemini", statusCode,
                        "Gemini AI service is temporarily unavailable (HTTP " + statusCode + "). Failing over to Groq.", true);
            } else if (statusCode == 400 || statusCode == 403) {
                throw new AiProviderException("gemini", statusCode,
                        "Gemini API rejected request (" + statusCode + "): " + errorBody, true);
            } else {
                throw new AiProviderException("gemini", statusCode,
                        "Gemini API client error (" + statusCode + "): " + errorBody, false);
            }
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (cancellationToken != null && cancellationToken.get()) {
                    log.info("Gemini stream cancelled by client");
                    break;
                }

                line = line.trim();
                if (line.isEmpty() || line.startsWith(":")) {
                    continue;
                }

                if (line.startsWith("data:")) {
                    String data = line.substring(5).trim();
                    try {
                        GeminiResponse chunk = objectMapper.readValue(data, GeminiResponse.class);
                        if (chunk != null && chunk.candidates != null && !chunk.candidates.isEmpty()) {
                            Candidate candidate = chunk.candidates.get(0);
                            if (candidate.content != null && candidate.content.parts != null) {
                                for (ContentPart part : candidate.content.parts) {
                                    if (part.text != null && !part.text.isEmpty()) {
                                        chunkConsumer.accept(part.text);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.debug("Skipping unparseable Gemini SSE line: {}", data);
                    }
                }
            }
        }
    }

    private GeminiRequest buildRequestPayload(String systemInstruction, List<ChatMessagePayload> history) {
        GeminiRequest requestPayload = new GeminiRequest();

        if (systemInstruction != null && !systemInstruction.isBlank()) {
            requestPayload.systemInstruction = new ContentPartContainer(
                    List.of(new ContentPart(systemInstruction)));
        }

        List<ContentMessage> contents = new ArrayList<>();
        if (history != null) {
            for (ChatMessagePayload msg : history) {
                String geminiRole = "user".equalsIgnoreCase(msg.role()) ? "user" : "model";
                contents.add(new ContentMessage(geminiRole, List.of(new ContentPart(msg.text()))));
            }
        }
        requestPayload.contents = contents;
        requestPayload.generationConfig = Map.of(
                "temperature", 0.7,
                "maxOutputTokens", maxTokens,
                "topP", 0.95);

        return requestPayload;
    }

    // Records & Wire Format DTOs
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GeminiRequest {
        @JsonProperty("system_instruction")
        public ContentPartContainer systemInstruction;
        public List<ContentMessage> contents;
        public Map<String, Object> generationConfig;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ContentPartContainer {
        public List<ContentPart> parts;

        public ContentPartContainer() {}

        public ContentPartContainer(List<ContentPart> parts) {
            this.parts = parts;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ContentMessage {
        public String role;
        public List<ContentPart> parts;

        public ContentMessage() {}

        public ContentMessage(String role, List<ContentPart> parts) {
            this.role = role;
            this.parts = parts;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ContentPart {
        public String text;

        public ContentPart() {}

        public ContentPart(String text) {
            this.text = text;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GeminiResponse {
        public List<Candidate> candidates;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Candidate {
        public ContentMessage content;
        public String finishReason;
    }
}
