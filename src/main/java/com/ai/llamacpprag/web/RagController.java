package com.ai.llamacpprag.web;

import com.ai.llamacpprag.service.DocumentIngestionService;
import com.ai.llamacpprag.service.RagChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;

/**
 * Controller for managing and interacting with the RAG (Retrieval-Augmented Generation) system.
 */
@RestController
@RequestMapping("/api")
public class RagController {

    /**
     * Services for document ingestion and chat interactions with the RAG system.
     */
    private final DocumentIngestionService ingestionService;

    /**
     * Service for chat interactions with the RAG system.
     */
    private final RagChatService chatService;

    /**
     * Constructs a RagController with the specified ingestion service and chat service.
     *
     * @param ingestionService Service for document ingestion
     * @param chatService      Service for chat interactions with the RAG system
     */
    public RagController(DocumentIngestionService ingestionService, RagChatService chatService) {
        this.ingestionService = ingestionService;
        this.chatService = chatService;
    }

    /**
     * Initiates document ingestion from a local folder and returns the result.
     *
     * @return ResponseEntity containing the ingestion result
     * @throws Exception If an error occurs during ingestion
     */
    @PostMapping("/ingest")
    public ResponseEntity<?> ingest() throws Exception {
        var result = ingestionService.ingestLocalFolder(Path.of("./data"));
        return ResponseEntity.ok(result);
    }

    /**
     * Initiates a chat interaction with the RAG system and returns the answer.
     *
     * @param q The question to ask
     * @return ResponseEntity containing the chat response
     */
    @GetMapping("/chat")
    public ResponseEntity<?> chat(@RequestParam("q") String q) {
        var answer = chatService.ask(q);
        return ResponseEntity.ok(new ChatResponse(q, answer));
    }

    /**
     * Represents the response to a chat interaction with the RAG system.
     *
     * @param question The question asked
     * @param answer   The answer provided by the RAG system
     */
    public record ChatResponse(String question, String answer) {
    }
}
