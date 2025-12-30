package com.ai.llamacpprag.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ChatModelPrimaryConfig}.
 */
@ExtendWith(MockitoExtension.class)
class ChatModelPrimaryConfigTest {

    private final ChatModelPrimaryConfig config = new ChatModelPrimaryConfig();

    @Mock
    private OpenAiChatModel mockOpenAiChatModel;

    @Test
    void primaryChatModel_ReturnsOpenAiChatModel() {
        // Act
        ChatModel result = config.primaryChatModel(mockOpenAiChatModel);

        // Assert
        assertNotNull(result);
        assertSame(mockOpenAiChatModel, result);
    }

    @Test
    void primaryChatModel_ReturnsSameInstance() {
        // Act
        ChatModel result1 = config.primaryChatModel(mockOpenAiChatModel);
        ChatModel result2 = config.primaryChatModel(mockOpenAiChatModel);

        // Assert - both should be the same mocked instance
        assertSame(result1, result2);
    }
}
