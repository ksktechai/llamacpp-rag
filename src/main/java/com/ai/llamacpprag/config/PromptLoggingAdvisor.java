package com.ai.llamacpprag.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;

/**
 * Advisor that logs the final prompt sent to the LLM (after RAG augmentation).
 */
public class PromptLoggingAdvisor implements BaseAdvisor {

    private static final Logger logger = LoggerFactory.getLogger(PromptLoggingAdvisor.class);

    @Override
    public ChatClientRequest before(ChatClientRequest request, AdvisorChain chain) {
        logPrompt(request, "REQ");
        return request;
    }

    @Override
    public ChatClientResponse after(ChatClientResponse response, AdvisorChain chain) {
        logger.info("AUGMENTED PROMPT RESPONSE [{}]: {}", response.chatResponse(), response.context());
        return response;
    }

    private void logPrompt(ChatClientRequest request, String type) {
        try {
            logger.info("AUGMENTED PROMPT [{}]: {}", type, request.prompt());
        } catch (Exception e) {
            logger.warn("Failed to log augmented prompt", e);
        }
    }

    @Override
    public String getName() {
        return "PromptLoggingAdvisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
