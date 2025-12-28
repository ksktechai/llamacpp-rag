package com.ai.llamacpprag.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration for primary chat model, prioritizing OpenAI.
 */
@Configuration
public class ChatModelPrimaryConfig {

    /**
     * Configure the primary chat model, prioritizing OpenAI.
     *
     * @param openAiChatModel The OpenAI chat model
     * @return The configured primary chat model
     */
    @Bean
    @Primary
    public ChatModel primaryChatModel(OpenAiChatModel openAiChatModel) {
        return openAiChatModel;
    }
}