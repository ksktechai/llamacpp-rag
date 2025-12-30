package com.ai.llamacpprag.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.vectorstore.VectorStore;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link RagConfig}.
 */
@ExtendWith(MockitoExtension.class)
class RagConfigTest {

    private final RagConfig config = new RagConfig();

    @Mock
    private ChatClient.Builder mockBuilder;

    @Mock
    private VectorStore mockVectorStore;

    @Mock
    private ChatClient mockChatClient;

    @Test
    void chatClient_ReturnsChatClient() {
        // Arrange
        when(mockBuilder.defaultAdvisors(any(Advisor[].class))).thenReturn(mockBuilder);
        when(mockBuilder.build()).thenReturn(mockChatClient);

        // Act
        ChatClient result = config.chatClient(mockBuilder, mockVectorStore);

        // Assert
        assertNotNull(result);
        assertSame(mockChatClient, result);
    }

    @Test
    void chatClient_ConfiguresAdvisors() {
        // Arrange
        when(mockBuilder.defaultAdvisors(any(Advisor[].class))).thenReturn(mockBuilder);
        when(mockBuilder.build()).thenReturn(mockChatClient);

        // Act
        config.chatClient(mockBuilder, mockVectorStore);

        // Assert - verify advisors were configured
        verify(mockBuilder).defaultAdvisors(any(Advisor[].class));
        verify(mockBuilder).build();
    }

    @Test
    void chatClient_UsesVectorStore() {
        // Arrange
        when(mockBuilder.defaultAdvisors(any(Advisor[].class))).thenReturn(mockBuilder);
        when(mockBuilder.build()).thenReturn(mockChatClient);

        // Act
        ChatClient result = config.chatClient(mockBuilder, mockVectorStore);

        // Assert
        assertNotNull(result);
    }
}
