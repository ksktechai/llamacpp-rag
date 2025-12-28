package com.ai.llamacpprag.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Caffeine cache configuration for chat answers.
 */
@Configuration
public class ChatCacheConfig {

    /**
     * Configure the chat answer cache with a maximum size of 500 entries and a 30-minute expiration time.
     *
     * @return The configured chat answer cache
     */
    @Bean
    public Cache<String, String> chatAnswerCache() {
        return Caffeine.newBuilder()
                .maximumSize(500)                 // adjust for demos
                .expireAfterWrite(Duration.ofMinutes(30))
                .recordStats()
                .build();
    }
}