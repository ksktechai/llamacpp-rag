package com.ai.llamacpprag.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RAG configuration.
 */
@Configuration
public class RagConfig {
    /**
     * Configure the ChatClient bean with retrieval augmentation.
     *
     * @param builder      The ChatClient builder
     * @param vectorStore  The vector store for document retrieval
     * @return The configured ChatClient bean
     */
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder,
                                 VectorStore vectorStore) {

        var ragAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .vectorStore(vectorStore)
                        .similarityThreshold(0.50)
                        .topK(3)
                        .build())
                .build();

        return builder
                .defaultAdvisors(ragAdvisor)
                .build();
    }
}