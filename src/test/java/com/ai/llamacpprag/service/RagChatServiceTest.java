package com.ai.llamacpprag.service;

import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RagChatServiceTest {

    @Mock
    private ChatClient chatClient;

    @Mock
    private Cache<String, String> cache;

    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    private ChatClient.CallResponseSpec responseSpec;

    private RagChatService service;

    @BeforeEach
    void setUp() {
        service = new RagChatService(chatClient, cache);
    }

    @Test
    void ask_CacheHit() {
        String question = "What is RAG?";
        String expectedAnswer = "Retrieval Augmented Generation";

        // Mock cache hit
        when(cache.getIfPresent(anyString())).thenReturn(expectedAnswer);

        String answer = service.ask(question);

        assertEquals(expectedAnswer, answer);
        // Verify we looked in cache
        verify(cache).getIfPresent(contains("what is rag"));
        // Verify we DID NOT call chat client
        verifyNoInteractions(chatClient);
    }

    @Test
    void ask_CacheMiss_ValidResponse() {
        String question = "What is RAG?";
        String expectedAnswer = "Retrieval Augmented Generation";

        // Mock cache miss
        when(cache.getIfPresent(anyString())).thenReturn(null);

        // Mock ChatClient chain
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);
        when(responseSpec.content()).thenReturn(expectedAnswer);

        String answer = service.ask(question);

        assertEquals(expectedAnswer, answer);

        // Verify cache put
        verify(cache).put(contains("what is rag"), eq(expectedAnswer));
    }

    @Test
    void ask_CacheMiss_DontCacheIDontKnow() {
        String question = "Unknown?";
        String badAnswer = "I doN't kNow";

        when(cache.getIfPresent(anyString())).thenReturn(null);

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);
        when(responseSpec.content()).thenReturn(badAnswer);

        String answer = service.ask(question);

        assertEquals(badAnswer, answer);
        // Should NOT cache "I don't know"
        verify(cache, never()).put(anyString(), anyString());
    }

    @Test
    void ask_CacheMiss_DontCacheEmpty() {
        String question = "Unknown?";

        when(cache.getIfPresent(anyString())).thenReturn(null);

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);
        when(responseSpec.content()).thenReturn("   ");

        String answer = service.ask(question);

        // Should NOT cache empty
        verify(cache, never()).put(anyString(), anyString());
    }
}
