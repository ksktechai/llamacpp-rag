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

    /**
     * Logs the prompt before augmentation.
     * @param request the ChatClientRequest containing the prompt
     * @param chain the advisor chain
     * @return the modified ChatClientRequest
     */
    @Override
    public ChatClientRequest before(ChatClientRequest request, AdvisorChain chain) {
        logPrompt(request, "REQ");
        return request;
    }

    /**
     * Logs the augmented prompt after augmentation.
     * @param response the ChatClientResponse containing the augmented prompt
     * @param chain the advisor chain
     * @return the modified ChatClientResponse
     */
    @Override
    public ChatClientResponse after(ChatClientResponse response, AdvisorChain chain) {
        logger.info("AUGMENTED PROMPT RESPONSE [{}]: {}", response.chatResponse(), response.context());
        return response;
    }

    /**
     * Logs the prompt.
     * @param request the ChatClientRequest containing the prompt
     * @param type the type of prompt (e.g., "REQ" or "RESP")
     */
    private void logPrompt(ChatClientRequest request, String type) {
        try {
            logger.info("AUGMENTED PROMPT [{}]: {}", type, request.prompt());
        } catch (Exception e) {
            logger.warn("Failed to log augmented prompt", e);
        }
    }

    /**
     * Returns the name of the advisor.
     * @return the name of the advisor
     */
    @Override
    public String getName() {
        return "PromptLoggingAdvisor";
    }

    /**
     * Returns the order of the advisor.
     * @return the order of the advisor
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
