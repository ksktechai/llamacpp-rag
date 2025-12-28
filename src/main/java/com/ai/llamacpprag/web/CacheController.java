package com.ai.llamacpprag.web;

import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller for managing and monitoring the chat answer cache.
 */
@RestController
public class CacheController {

    /**
     * Cache for storing and retrieving chat answers.
     */
    private final Cache<String, String> chatAnswerCache;

    /**
     * Constructs a CacheController with the specified chat answer cache.
     *
     * @param chatAnswerCache Cache for storing and retrieving chat answers
     */
    public CacheController(Cache<String, String> chatAnswerCache) {
        this.chatAnswerCache = chatAnswerCache;
    }

    /**
     * Get cache statistics.
     *
     * @return Map containing cache statistics
     */
    @GetMapping("/api/cache/stats")
    public Map<String, Object> stats() {
        var s = chatAnswerCache.stats();
        return Map.of(
                "hitCount", s.hitCount(),
                "missCount", s.missCount(),
                "hitRate", s.hitRate(),
                "evictionCount", s.evictionCount(),
                "estimatedSize", chatAnswerCache.estimatedSize()
        );
    }
}