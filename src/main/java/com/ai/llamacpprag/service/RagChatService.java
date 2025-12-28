package com.ai.llamacpprag.service;

import com.github.benmanes.caffeine.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * Service for interacting with the RAG (Retrieval-Augmented Generation) system.
 */
@Service
public class RagChatService {

    /**
     * Logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(RagChatService.class);

    /**
     * Chat client for interacting with the RAG system.
     */
    private final ChatClient chatClient;

    /**
     * Cache for storing and retrieving chat answers.
     */
    private final Cache<String, String> chatAnswerCache;

    /**
     * Constructs a RagChatService with the specified chat client and cache.
     *
     * @param chatClient      Chat client for interacting with the RAG system
     * @param chatAnswerCache Cache for storing and retrieving chat answers
     */
    public RagChatService(ChatClient chatClient, Cache<String, String> chatAnswerCache) {
        this.chatClient = chatClient;
        this.chatAnswerCache = chatAnswerCache;
    }

    /**
     * Asks the RAG system a question and retrieves the answer, caching non-empty
     * responses.
     *
     * @param question The question to ask
     * @return The answer to the question
     */
    public String ask(String question) {
        logger.info("QUESTION: {}", question);
        String key = cacheKey(question);

        String cached = chatAnswerCache.getIfPresent(key);
        if (cached != null) {
            logger.info("CACHE HIT for question='{}'", question);
            return cached;
        }
        logger.info("CACHE MISS");

        logger.info("AI REQUEST: {}", question);
        String answer = chatClient.prompt()
                .user(question)
                .call()
                .content();
        logger.info("AI RESPONSE: {}", answer);

        // Cache only non-empty answers (avoid caching "I don't know" during
        // instability)
        if (answer != null && !answer.isBlank() && !answer.trim().equalsIgnoreCase("I don't know")) {
            chatAnswerCache.put(key, answer);
            logger.info("Inserting answer to cache");
        }

        return answer;
    }

    /**
     * Generates a cache key based on the question, including model, topK/threshold,
     * and doc filter scope.
     *
     * @param question The question to generate a cache key for
     * @return The generated cache key
     */
    private String cacheKey(String question) {
        // You can expand this to include:
        // - active model name
        // - topK/threshold
        // - doc filter scope
        return "q:" + question.trim().toLowerCase();
    }
}
