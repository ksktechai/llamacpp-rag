# 📬 Postman Collection – Local RAG APIs

This Postman collection provides ready-made requests to test the Local RAG System built with:
-	Spring Boot 4
-	llama.cpp (chat)
-	Ollama (embeddings)
-	PostgreSQL + pgvector

The collection is designed to validate:
-	Document ingestion
-	Retrieval-Augmented Generation (RAG)
-	Question answering against an ingested PDF
-	Caching of chat answers

## 📁 Files
```jsunicoderegexp
docs/
├── llama-cpp.postman_collection.json
└── README-postman.md
```

## 🔄 Recommended Execution Order

### 1️⃣ Ingest documents (required)

Request: rag-pdf

```jsunicoderegexp
POST http://localhost:8080/api/ingest
```

Purpose:
-	Reads documents from ./data
-	Chunks and embeds them
-	Stores embeddings in pgvector

⚠️ This must be run once before querying.

### 2️⃣ Run chat / RAG queries

All remaining requests are GET calls to:

```jsunicoderegexp
GET http://localhost:8080/api/chat?q=...
```

They demonstrate different question types:

## 📬 Postman Collection – Requests Overview

| Request Name | Method | Endpoint | Description |
|-------------|--------|----------|-------------|
| rag-pdf | POST | `/api/ingest` | Ingests all documents from the `./data` folder (PDF/TXT/MD). Chunks text, generates embeddings, and stores them in pgvector. **Must be run before any chat queries.** |
| query-1 | GET | `/api/chat` | Retrieves the key design principles for AI agents discussed in the startup technical guide. |
| query-2 | GET | `/api/chat` | Answers the foundational question: *What is an AI agent?*, grounded in the guide. |
| query-3 | GET | `/api/chat` | Explains the recommended agent architecture in a simplified, ELI5-style manner. |
| query-4 | GET | `/api/chat` | Provides a step-by-step checklist for building a production-ready AI agent as per the guide. |
| query-5 | GET | `/api/chat` | Explains how to use Google AI Agents, based strictly on the startup technical guide. |
| query-6 | GET | `/api/chat` | Lists all learning resources mentioned in the guide for understanding and building AI agents. |
| query-7 | GET | `/api/chat` | Summarizes the key takeaways from the startup technical guide. |
| query-8 | GET | `/api/chat` | Explains what ADK (Agent Development Kit) stands for and how it is used, based on the guide. |
| query-9 | GET | `/api/chat` | Provides a detailed definition of an LLM Agent, grounded in the document content. |
| query-10 | GET | `/api/chat` | Explains MCP (Model Context Protocol) and how it helps AI agents. |
| query-11 | GET | `/api/cache/stats` | Returns in-memory cache statistics (hits, misses, hit rate, size). Useful for validating query caching behavior. |


> 💡 Tip  
> Run **rag-pdf** first to ingest documents.  
> All `/api/chat` queries rely on the ingested content and demonstrate Retrieval-Augmented Generation (RAG).


### 🧠 What this validates

Using this collection confirms:
-	✅ Embeddings are generated correctly
-	✅ Vector similarity search works
-	✅ Context is injected into prompts
-	✅ llama.cpp generates grounded answers
-	✅ End-to-end RAG pipeline is functional

### 📝 Notes
- The collection is designed to validate document ingestion, retrieval-augmented generation (RAG), question answering against an ingested PDF, and caching of chat answers.
- The recommended execution order is to first ingest documents and then run chat/RAG queries.
- The collection includes ready-made requests for different question types, demonstrating the system's capabilities.
- The collection is compatible with both llama.cpp and Ollama for chat and embeddings, respectively.

