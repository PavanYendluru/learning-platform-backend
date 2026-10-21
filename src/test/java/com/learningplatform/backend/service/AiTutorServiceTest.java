package com.learningplatform.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learningplatform.backend.client.GeminiClient;
import com.learningplatform.backend.dto.*;
import com.learningplatform.backend.entity.*;
import com.learningplatform.backend.enums.AiMessageRole;
import com.learningplatform.backend.enums.Role;
import com.learningplatform.backend.enums.TeachingStyle;
import com.learningplatform.backend.exception.BadRequestException;
import com.learningplatform.backend.exception.ResourceNotFoundException;
import com.learningplatform.backend.repository.*;
import com.learningplatform.backend.service.impl.AiTutorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiTutorServiceTest {

    @Mock
    private AiConversationRepository conversationRepository;

    @Mock
    private AiMessageRepository messageRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private GeminiClient geminiClient;

    @Mock
    private AiOrchestrationService orchestrationService;

    private ObjectMapper objectMapper;
    private AiTutorServiceImpl aiTutorService;
    private User testUser;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        aiTutorService = new AiTutorServiceImpl(
                conversationRepository,
                messageRepository,
                resourceRepository,
                topicRepository,
                currentUserService,
                geminiClient,
                orchestrationService,
                objectMapper,
                15,
                4000
        );

        testUser = new User();
        testUser.setName("Pavan Yendluru");
        testUser.setEmail("pavanyendluru@gmail.com");
        testUser.setRole(Role.USER);

        try {
            var idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(testUser, 1L);
        } catch (Exception ignored) {}

        lenient().when(currentUserService.getCurrentUser()).thenReturn(testUser);
    }

    @Test
    void testCreateConversation_Success() {
        AiConversation savedConv = new AiConversation();
        savedConv.setUser(testUser);
        savedConv.setTitle("Learning Spring Boot");

        when(conversationRepository.save(any(AiConversation.class))).thenReturn(savedConv);

        CreateConversationRequest req = new CreateConversationRequest("Learning Spring Boot", null, null);
        AiConversationResponse res = aiTutorService.createConversation(req);

        assertNotNull(res);
        assertEquals("Learning Spring Boot", res.getTitle());
        verify(conversationRepository).save(any(AiConversation.class));
    }

    @Test
    void testGetConversation_OwnershipValidation_ThrowsWhenNotFound() {
        when(conversationRepository.findByIdAndUserId(eq(999L), eq(1L))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> aiTutorService.getConversation(999L));
    }

    @Test
    void testSendMessage_SuccessWithFollowUpsAndProvider() {
        AiConversation conversation = new AiConversation();
        conversation.setUser(testUser);
        conversation.setTitle("Java Generics");

        when(conversationRepository.findByIdAndUserId(eq(10L), eq(1L))).thenReturn(Optional.of(conversation));
        when(resourceRepository.search(any(), any(), any(), any(), any(), anyBoolean(), any(Pageable.class)))
                .thenReturn(Page.empty());

        when(orchestrationService.generateResponse(anyString(), anyList()))
                .thenReturn(new AiOrchestrationService.GenerationResult(
                        "Java generics provide compile-time type safety.\n\n[SUGGESTION] What is type erasure?\n[SUGGESTION] Show a wildcard example",
                        "gemini",
                        "gemini-3.1-flash-lite"
                ));

        when(messageRepository.save(any(AiMessage.class))).thenAnswer(inv -> inv.getArgument(0));

        AiMessageRequest msgReq = new AiMessageRequest("Explain Java Generics", TeachingStyle.BEGINNER, null, null);
        AiMessageResponse response = aiTutorService.sendMessage(10L, msgReq);

        assertNotNull(response);
        assertEquals(AiMessageRole.ASSISTANT, response.getRole());
        assertEquals("gemini", response.getProvider());
        assertTrue(response.getContent().contains("compile-time type safety"));
        assertEquals(2, response.getSuggestedFollowUps().size());
        assertEquals("What is type erasure?", response.getSuggestedFollowUps().get(0));
    }

    @Test
    void testSendMessage_ExceedsMaxMessageLength_ThrowsBadRequest() {
        AiConversation conversation = new AiConversation();
        conversation.setUser(testUser);

        when(conversationRepository.findByIdAndUserId(eq(10L), eq(1L))).thenReturn(Optional.of(conversation));

        String hugeText = "A".repeat(4001);
        AiMessageRequest msgReq = new AiMessageRequest(hugeText, TeachingStyle.GENERAL, null, null);

        assertThrows(BadRequestException.class, () -> aiTutorService.sendMessage(10L, msgReq));
    }

    @Test
    void testSendMessage_EmptyContent_ThrowsBadRequest() {
        AiConversation conversation = new AiConversation();
        conversation.setUser(testUser);

        when(conversationRepository.findByIdAndUserId(eq(10L), eq(1L))).thenReturn(Optional.of(conversation));

        AiMessageRequest msgReq = new AiMessageRequest("   ", TeachingStyle.GENERAL, null, null);

        assertThrows(BadRequestException.class, () -> aiTutorService.sendMessage(10L, msgReq));
    }
}
