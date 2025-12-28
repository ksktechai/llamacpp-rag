# 🐳 Docker Commands Reference – Local RAG System

This document lists all Docker and Docker Compose commands used to run, debug, and maintain the Local RAG system built with:
-	PostgreSQL + pgvector
-	Ollama (embeddings)
-	llama.cpp (chat / generation)

---

## 📦 Services Overview

- **PostgreSQL**: Database service for storing embeddings and metadata.
- **pgvector**: PostgreSQL extension for vector similarity search.
- **Ollama**: Containerized model serving for embeddings.
- **llama.cpp**: Containerized model serving for chat and generation tasks.
- **Volumes**: Persistent storage for Docker containers.

## ▶️ Starting the System

### Start all services (recommended)

```bash
docker compose up -d
```

This starts:
- PostgreSQL + pgvector
- Ollama
- llama.cpp

---

#### Start a single service

```bash
docker compose up -d <service_name>
```

Replace `<service_name>` with the name of the service you want to start, such as `postgres`, `ollama`, or `llama`.

e.g.

```bash
docker compose up -d llamacpp
docker compose up -d ollama
docker compose up -d postgres
```
---

### ⏹️ Stopping Services

#### Stop all services (containers remain)

```bash
docker compose stop
```

#### Stop and remove containers (keep volumes)

```bash
docker compose down
```
---

### 🔄 Restarting Services

#### Restart all services

```bash
docker compose restart
```

#### Restart a single service

```bash
docker compose restart <service_name>
```

Replace `<service_name>` with the name of the service you want to restart, such as `postgres`, `ollama`, or `llama`.

e.g.
```bash
docker compose restart llamacpp
```

---

## 🔁 Recreate Containers (NO data loss)

### Recreate llama.cpp only (safe)

```bash
docker compose up -d --force-recreate llamacpp
```

✅ Safe because:
-	Model files are mounted via volume
-	Database is untouched

### ⚠️ Full Reset (DATA LOSS)

Stop everything and delete volumes

```bash
docker compose down -v
```

🚨 This will:
-	Delete pgvector embeddings
-	Delete Ollama downloaded models
-	Require re-ingest

---

### 🔍 Inspecting Running Containers

#### List running containers

```bash
docker ps
```

#### List all containers (including stopped)

```bash
docker ps -a
```

---

## 🧠 llama.cpp Debugging

### View llama.cpp logs

```bash
docker logs llamacpp_server
```

### Follow logs live

```bash
docker logs -f --tail 100 llamacpp_server
```

Common things to check in logs
-	Model loaded successfully
-	--embeddings enabled
-	Pooling type (mean)
-	Context size (--ctx-size)

---

## 🧬 Ollama Commands

### Pull embedding model (once)

```bash
docker exec -it ollama ollama pull nomic-embed-text
```

### List downloaded models

```bash
docker exec -it ollama ollama list

NAME                       ID              SIZE      MODIFIED     
nomic-embed-text:latest    0a109f422b47    274 MB    17 hours ago    
```

### Check Ollama health

```bash
curl http://localhost:11434/api/tags

{"models":[{"name":"nomic-embed-text:latest","model":"nomic-embed-text:latest","modified_at":"2025-12-27T14:15:13.672899006Z","size":274302450,"digest":"0a109f422b47e3a30ba2b10eca18548e944e8a23073ee3f3e947efcf3c45e59f","details":{"parent_model":"","format":"gguf","family":"nomic-bert","families":["nomic-bert"],"parameter_size":"137M","quantization_level":"F16"}}]}   
```

---

## 🗄️ PostgreSQL + pgvector

### Connect to Postgres shell

```bash
docker exec -it llamacpp_rag_pg psql -U rag -d ragdb
```

---

### 🗄️ PostgreSQL + pgvector

Connect to Postgres shell

```bash
docker exec -it llamacpp_rag_pg psql -U rag -d ragdb

psql (16.11 (Debian 16.11-1.pgdg12+1))
Type "help" for help.

ragdb=# 
ragdb=# 
ragdb=# 
ragdb=# quit
```
---

### Check vector store table

```bash
docker exec -it llamacpp_rag_pg \
  psql -U rag -d ragdb \
  -c "\d+ public.vector_store"
  
                                                Table "public.vector_store"
  Column   |    Type     | Collation | Nullable |      Default       | Storage  | Compression | Stats target | Description 
-----------+-------------+-----------+----------+--------------------+----------+-------------+--------------+-------------
 id        | uuid        |           | not null | uuid_generate_v4() | plain    |             |              | 
 content   | text        |           |          |                    | extended |             |              | 
 metadata  | json        |           |          |                    | extended |             |              | 
 embedding | vector(768) |           |          |                    | external |             |              | 
Indexes:
    "vector_store_pkey" PRIMARY KEY, btree (id)
    "spring_ai_vector_index" hnsw (embedding vector_cosine_ops)
Access method: heap
```
---

### Inspect stored metadata

```bash
docker exec -it llamacpp_rag_pg \
  psql -U rag -d ragdb \
  -c "SELECT metadata FROM vector_store LIMIT 1;"
  
   metadata                                                                                                                   
----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 {"page": 2, "source": "/Users/senthilkumar/google-ai/llamacpp-rag/./data/startup_technical_guide_ai_agents_final.pdf", "fileType": "pdf", "chunk_index": 0, "total_chunks": 1, "parent_document_id": "d7f529c1-63b1-4757-8f24-3d2478d71b46"}
(1 row)
```

```bash
docker exec -it llamacpp_rag_pg \
  psql -U rag -d ragdb \
  -c "SELECT left(content, 300) AS snippet, metadata FROM vector_store WHERE content ILIKE '%ADK%' LIMIT 5;"
```

### Drop Table

```bash
docker exec -it llamacpp_rag_pg \
  psql -U rag -d ragdb \
  -c "DROP TABLE IF EXISTS public.vector_store CASCADE;"
```

### Count Records

```bash
docker exec -it llamacpp_rag_pg psql -U rag -d ragdb -c "SELECT COUNT(*) FROM vector_store;"
```

---

## 🌐 API Connectivity Checks

### Check llama.cpp health
```bash
curl http://localhost:8081/v1/embeddings
```

---

## 🧹 Cleanup Commands

### Remove unused images
```bash
docker image prune
```

### Remove unused containers
```bash
docker container prune
```

### Remove unused volumes (be careful)
```bash
docker volume prune
```


