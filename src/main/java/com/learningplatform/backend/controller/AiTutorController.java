package com.learningplatform.backend.controller;

import com.learningplatform.backend.client.GeminiClient;
import com.learningplatform.backend.dto.*;
import com.learningplatform.backend.service.AiOrchestrationService;
import com.learningplatform.backend.service.AiTutorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI Tutor", description = "Endpoints for AI Tutor conversations, messages, streaming, and platform guidance")
public class AiTutorController {

    private final AiTutorService aiTutorService;
    private final GeminiClient geminiClient;
    private final AiOrchestrationService orchestrationService;

    public AiTutorController(
            AiTutorService aiTutorService,
            GeminiClient geminiClient,
            AiOrchestrationService orchestrationService) {
        this.aiTutorService = aiTutorService;
        this.geminiClient = geminiClient;
        this.orchestrationService = orchestrationService;
    }

    @GetMapping("/status")
    @Operation(summary = "Check AI Tutor configuration status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatus() {
        Map<String, Object> status = Map.of(
                "configured", orchestrationService.isGeminiConfigured() || orchestrationService.isGroqConfigured(),
                "model", orchestrationService.getPrimaryModel(),
                "primaryProvider", "gemini",
                "primaryModel", orchestrationService.getPrimaryModel(),
                "primaryConfigured", orchestrationService.isGeminiConfigured(),
                "fallbackProvider", "groq",
                "fallbackModel", orchestrationService.getFallbackModel(),
                "fallbackConfigured", orchestrationService.isGroqConfigured(),
                "fallbackEnabled", orchestrationService.isFallbackEnabled()
        );
        return ResponseEntity.ok(ApiResponse.success("AI Tutor status", status));
    }

    @PostMapping("/conversations")
    @Operation(summary = "Create a new AI conversation")
    public ResponseEntity<ApiResponse<AiConversationResponse>> createConversation(
            @Valid @RequestBody(required = false) CreateConversationRequest request) {

        AiConversationResponse response = aiTutorService.createConversation(
                request != null ? request : new CreateConversationRequest()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Conversation created successfully", response));
    }

    @GetMapping("/conversations")
    @Operation(summary = "List current user's AI conversations")
    public ResponseEntity<ApiResponse<PagedResponse<AiConversationResponse>>> listConversations(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        PagedResponse<AiConversationResponse> result = aiTutorService.listConversations(search, pageable);
        return ResponseEntity.ok(ApiResponse.success("Conversations retrieved successfully", result));
    }

    @GetMapping("/conversations/{id}")
    @Operation(summary = "Retrieve conversation history by ID")
    public ResponseEntity<ApiResponse<AiConversationResponse>> getConversation(@PathVariable Long id) {
        AiConversationResponse conversation = aiTutorService.getConversation(id);
        return ResponseEntity.ok(ApiResponse.success("Conversation retrieved successfully", conversation));
    }

    @PatchMapping("/conversations/{id}")
    @Operation(summary = "Update conversation title")
    public ResponseEntity<ApiResponse<AiConversationResponse>> updateTitle(
            @PathVariable Long id,
            @Valid @RequestBody UpdateConversationTitleRequest request) {

        AiConversationResponse updated = aiTutorService.updateConversationTitle(id, request);
        return ResponseEntity.ok(ApiResponse.success("Conversation title updated successfully", updated));
    }

    @DeleteMapping("/conversations/{id}")
    @Operation(summary = "Delete an AI conversation")
    public ResponseEntity<ApiResponse<Void>> deleteConversation(@PathVariable Long id) {
        aiTutorService.deleteConversation(id);
        return ResponseEntity.ok(ApiResponse.success("Conversation deleted successfully"));
    }

    @PostMapping("/conversations/{id}/messages")
    @Operation(summary = "Send a message and receive an AI Tutor response (synchronous fallback)")
    public ResponseEntity<ApiResponse<AiMessageResponse>> sendMessage(
            @PathVariable Long id,
            @Valid @RequestBody AiMessageRequest request) {

        AiMessageResponse response = aiTutorService.sendMessage(id, request);
        return ResponseEntity.ok(ApiResponse.success("AI response generated successfully", response));
    }

    @PostMapping(value = "/conversations/{id}/messages/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Stream AI Tutor response chunks in real-time with automatic Gemini-to-Groq fallback")
    public SseEmitter streamMessage(
            @PathVariable Long id,
            @Valid @RequestBody AiMessageRequest request) {

        return aiTutorService.streamMessage(id, request);
    }
}
