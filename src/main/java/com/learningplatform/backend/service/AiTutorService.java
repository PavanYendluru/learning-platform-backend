package com.learningplatform.backend.service;

import com.learningplatform.backend.dto.*;
import org.springframework.data.domain.Pageable;

public interface AiTutorService {

    AiConversationResponse createConversation(CreateConversationRequest request);

    PagedResponse<AiConversationResponse> listConversations(String search, Pageable pageable);

    AiConversationResponse getConversation(Long id);

    AiConversationResponse updateConversationTitle(Long id, UpdateConversationTitleRequest request);

    void deleteConversation(Long id);

    AiMessageResponse sendMessage(Long conversationId, AiMessageRequest request);

    org.springframework.web.servlet.mvc.method.annotation.SseEmitter streamMessage(Long conversationId, AiMessageRequest request);
}
