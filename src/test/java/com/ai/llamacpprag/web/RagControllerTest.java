package com.ai.llamacpprag.web;

import com.ai.llamacpprag.service.DocumentIngestionService;
import com.ai.llamacpprag.service.RagChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RagControllerTest {

    @Mock
    private DocumentIngestionService ingestionService;

    @Mock
    private RagChatService chatService;

    private RagController controller;

    @BeforeEach
    void setUp() {
        controller = new RagController(ingestionService, chatService);
    }

    @Test
    void ingest_Success() throws Exception {
        var result = new DocumentIngestionService.IngestResult(5, 10, List.of("warning1"));

        when(ingestionService.ingestLocalFolder(any(Path.class))).thenReturn(result);

        ResponseEntity<?> response = controller.ingest();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(result, response.getBody());
    }

    @Test
    void chat_Success() {
        String query = "Hello";
        String answer = "World";

        when(chatService.ask(query)).thenReturn(answer);

        ResponseEntity<?> response = controller.chat(query);

        assertEquals(200, response.getStatusCode().value());
        RagController.ChatResponse body = (RagController.ChatResponse) response.getBody();
        assertEquals(query, body.question());
        assertEquals(answer, body.answer());
    }
}
