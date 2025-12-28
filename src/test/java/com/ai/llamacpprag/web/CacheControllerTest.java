package com.ai.llamacpprag.web;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CacheControllerTest {

    @Mock
    private Cache<String, String> cache;

    private CacheController controller;

    @BeforeEach
    void setUp() {
        controller = new CacheController(cache);
    }

    @Test
    void stats_Success() {
        // Mock CacheStats
        CacheStats stats = mock(CacheStats.class);

        when(stats.hitCount()).thenReturn(10L);
        when(stats.missCount()).thenReturn(5L);
        when(stats.evictionCount()).thenReturn(2L);
        when(stats.hitRate()).thenReturn(0.66);
        // default 0 for others

        when(cache.stats()).thenReturn(stats);
        when(cache.estimatedSize()).thenReturn(100L);

        Map<String, Object> result = controller.stats();

        assertEquals(10L, result.get("hitCount"));
        assertEquals(5L, result.get("missCount"));
        assertEquals(2L, result.get("evictionCount"));
        // hitRate might be double
        assertEquals(0.66, result.get("hitRate"));
        assertEquals(100L, result.get("estimatedSize"));
    }
}
