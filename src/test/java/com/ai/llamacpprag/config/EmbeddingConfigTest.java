package com.ai.llamacpprag.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link EmbeddingConfig}.
 */
@ExtendWith(MockitoExtension.class)
class EmbeddingConfigTest {

    private final EmbeddingConfig config = new EmbeddingConfig();

    @Mock
    private OllamaEmbeddingModel mockOllamaEmbeddingModel;

    @Test
    void embeddingModel_ReturnsOllamaEmbeddingModel() {
        // Act
        EmbeddingModel result = config.embeddingModel(mockOllamaEmbeddingModel);

        // Assert
        assertNotNull(result);
        assertSame(mockOllamaEmbeddingModel, result);
    }

    @Test
    void embeddingModel_ReturnsSameInstance() {
        // Act
        EmbeddingModel result1 = config.embeddingModel(mockOllamaEmbeddingModel);
        EmbeddingModel result2 = config.embeddingModel(mockOllamaEmbeddingModel);

        // Assert - both should be the same mocked instance
        assertSame(result1, result2);
    }
}
