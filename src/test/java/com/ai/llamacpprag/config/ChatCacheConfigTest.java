package com.ai.llamacpprag.config;

import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ChatCacheConfig}.
 */
class ChatCacheConfigTest {

    private final ChatCacheConfig config = new ChatCacheConfig();

    @Test
    void chatAnswerCache_ReturnsConfiguredCache() {
        // Act
        Cache<String, String> cache = config.chatAnswerCache();

        // Assert
        assertNotNull(cache);
    }

    @Test
    void chatAnswerCache_HasMaximumSize() {
        // Arrange
        Cache<String, String> cache = config.chatAnswerCache();

        // Fill cache beyond max size to verify eviction
        for (int i = 0; i < 600; i++) {
            cache.put("key" + i, "value" + i);
        }

        // Force cleanup
        cache.cleanUp();

        // Assert - should have evicted entries to stay at/below 500
        assertTrue(cache.estimatedSize() <= 500);
    }

    @Test
    void chatAnswerCache_RecordsStats() {
        // Arrange
        Cache<String, String> cache = config.chatAnswerCache();

        // Act
        cache.put("key1", "value1");
        cache.getIfPresent("key1"); // hit
        cache.getIfPresent("key2"); // miss

        // Assert - stats should be recorded
        var stats = cache.stats();
        assertEquals(1, stats.hitCount());
        assertEquals(1, stats.missCount());
    }

    @Test
    void chatAnswerCache_SupportsBasicOperations() {
        // Arrange
        Cache<String, String> cache = config.chatAnswerCache();

        // Act
        cache.put("testKey", "testValue");
        String retrieved = cache.getIfPresent("testKey");

        // Assert
        assertEquals("testValue", retrieved);
    }
}
