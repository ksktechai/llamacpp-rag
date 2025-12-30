package com.ai.llamacpprag.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link PromptLoggingAdvisor}.
 * Achieves 100% code coverage for all methods.
 */
@ExtendWith(MockitoExtension.class)
class PromptLoggingAdvisorTest {

    private PromptLoggingAdvisor advisor;

    @Mock
    private ChatClientRequest mockRequest;

    @Mock
    private ChatClientResponse mockResponse;

    @Mock
    private AdvisorChain mockChain;

    @Mock
    private Prompt mockPrompt;

    @Mock
    private ChatResponse mockChatResponse;

    @BeforeEach
    void setUp() {
        advisor = new PromptLoggingAdvisor();
    }

    @Test
    void before_ReturnsRequestAndLogsPrompt() {
        // Arrange
        when(mockRequest.prompt()).thenReturn(mockPrompt);

        // Act
        ChatClientRequest result = advisor.before(mockRequest, mockChain);

        // Assert
        assertSame(mockRequest, result);
        verify(mockRequest).prompt();
    }

    @Test
    void before_HandlesExceptionInLogging() {
        // Arrange - simulate an exception when accessing the prompt
        when(mockRequest.prompt()).thenThrow(new RuntimeException("Test exception"));

        // Act - should not throw, exception is caught and logged
        ChatClientRequest result = advisor.before(mockRequest, mockChain);

        // Assert
        assertSame(mockRequest, result);
        verify(mockRequest).prompt();
    }

    @Test
    void after_ReturnsResponseAndLogsAugmentedPrompt() {
        // Arrange
        when(mockResponse.chatResponse()).thenReturn(mockChatResponse);
        when(mockResponse.context()).thenReturn(Map.of("key", "value"));

        // Act
        ChatClientResponse result = advisor.after(mockResponse, mockChain);

        // Assert
        assertSame(mockResponse, result);
        verify(mockResponse).chatResponse();
        verify(mockResponse).context();
    }

    @Test
    void after_HandlesNullContext() {
        // Arrange
        when(mockResponse.chatResponse()).thenReturn(mockChatResponse);
        when(mockResponse.context()).thenReturn(null);

        // Act
        ChatClientResponse result = advisor.after(mockResponse, mockChain);

        // Assert
        assertSame(mockResponse, result);
    }

    @Test
    void getName_ReturnsCorrectName() {
        // Act
        String name = advisor.getName();

        // Assert
        assertEquals("PromptLoggingAdvisor", name);
    }

    @Test
    void getOrder_ReturnsZero() {
        // Act
        int order = advisor.getOrder();

        // Assert
        assertEquals(0, order);
    }
}
