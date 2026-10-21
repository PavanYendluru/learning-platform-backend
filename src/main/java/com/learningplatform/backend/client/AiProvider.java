package com.learningplatform.backend.client;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public interface AiProvider {

    String getProviderName();

    String getModelName();

    boolean isConfigured();

    String generateContent(String systemInstruction, List<ChatMessagePayload> history);

    void streamContent(String systemInstruction,
                       List<ChatMessagePayload> history,
                       Consumer<String> chunkConsumer,
                       AtomicBoolean cancellationToken) throws Exception;
}
