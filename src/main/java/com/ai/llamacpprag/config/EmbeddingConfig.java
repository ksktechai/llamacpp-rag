package com.ai.llamacpprag.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration for embedding models, prioritizing Ollama over other models.
 */
@Configuration
public class EmbeddingConfig {

    /**
     * Configure the primary embedding model, prioritizing Ollama.
     *
     * @param ollamaEmbeddingModel The Ollama embedding model
     * @return The configured primary embedding model
     */
    @Bean
    @Primary
    public EmbeddingModel embeddingModel(OllamaEmbeddingModel ollamaEmbeddingModel) {
        return ollamaEmbeddingModel;
    }
}