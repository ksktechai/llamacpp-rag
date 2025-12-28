package com.ai.llamacpprag.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads local files under ./data, splits into chunks, and stores embeddings in
 * pgvector.
 * Supported: .txt, .md, .pdf (PDF text extracted via PDFBox).
 */
@Service
public class DocumentIngestionService {

    /**
     * Vector store for storing document embeddings
     */
    private final VectorStore vectorStore;

    /**
     * Model for generating document embeddings
     */
    @SuppressWarnings("unused")
    private final EmbeddingModel embeddingModel;

    /**
     * Constructs a DocumentIngestionService with the specified vector store and
     * embedding model.
     *
     * @param vectorStore    Vector store for storing document embeddings
     * @param embeddingModel Model for generating document embeddings
     */
    public DocumentIngestionService(VectorStore vectorStore, EmbeddingModel embeddingModel) {
        this.vectorStore = vectorStore;
        this.embeddingModel = embeddingModel;
    }

    /**
     * Ingests local files under the specified folder, splitting content into chunks
     * and storing embeddings in pgvector.
     * Supported file types: .txt, .md, .pdf (PDF text extracted via PDFBox).
     *
     * @param folder Path to the folder containing local files
     * @return IngestResult containing the number of documents and chunks processed,
     * and any warnings
     * @throws IOException If an I/O error occurs while accessing the folder or
     *                     files
     */
    public IngestResult ingestLocalFolder(Path folder) throws IOException {
        if (!Files.exists(folder)) {
            return new IngestResult(0, 0, List.of("Folder not found: " + folder.toAbsolutePath()));
        }

        List<Path> files = new ArrayList<>();
        try (var stream = Files.walk(folder)) {
            stream.filter(Files::isRegularFile)
                    .filter(p -> {
                        var name = p.getFileName().toString().toLowerCase();
                        return name.endsWith(".txt") || name.endsWith(".md") || name.endsWith(".pdf");
                    })
                    .forEach(files::add);
        }

        int rawDocs = 0;
        int chunks = 0;
        List<String> warnings = new ArrayList<>();

        var splitter = new TokenTextSplitter(
                350, // chunkSize (tokens) <-- WELL below 512
                200, // minChunkSizeChars
                50, // minChunkLengthToEmbed
                10_000, // maxNumChunks (effectively unlimited)
                true, // keepSeparator
                List.of('.', '\n', '?', '!')); // sane defaults

        for (Path file : files) {
            var name = file.getFileName().toString().toLowerCase();

            try {
                if (name.endsWith(".pdf")) {
                    // PDF: create one Document per page (better retrieval + source tracing)
                    List<Document> pdfDocs = readPdfAsDocuments(file, warnings);
                    if (pdfDocs.isEmpty())
                        continue;

                    rawDocs += pdfDocs.size();

                    List<Document> split = splitter.apply(pdfDocs);
                    chunks += split.size();

                    vectorStore.add(split);
                } else {
                    // TXT/MD: single Document for file
                    String content = Files.readString(file, StandardCharsets.UTF_8);
                    if (content == null || content.isBlank())
                        continue;

                    rawDocs++;

                    var doc = new Document(content);
                    doc.getMetadata().put("source", file.toAbsolutePath().toString());
                    doc.getMetadata().put("fileType", name.endsWith(".md") ? "md" : "txt");

                    List<Document> split = splitter.apply(List.of(doc));
                    chunks += split.size();

                    vectorStore.add(split);
                }
            } catch (Exception e) {
                warnings.add("Failed ingesting " + file + ": " + e.getMessage());
            }
        }

        return new IngestResult(rawDocs, chunks, warnings);
    }

    /**
     * Reads a PDF file and splits its content into multiple documents.
     *
     * @param pdfPath  Path to the PDF file
     * @param warnings List to collect warnings during ingestion
     * @return List of Document objects representing the split content
     */
    private List<Document> readPdfAsDocuments(Path pdfPath, List<String> warnings) {
        List<Document> docs = new ArrayList<>();

        // Extracts text from PDF pages into documents; collects warnings
        try (PDDocument pdf = Loader.loadPDF(pdfPath.toFile())) {

            int totalPages = pdf.getNumberOfPages();
            PDFTextStripper stripper = new PDFTextStripper();

            // Extracts text from each page into document
            for (int page = 1; page <= totalPages; page++) {
                stripper.setStartPage(page);
                stripper.setEndPage(page);

                String text = stripper.getText(pdf);
                if (text == null || text.isBlank()) {
                    // Could be scanned-image PDF or empty page
                    continue;
                }

                Document doc = new Document(text);
                doc.getMetadata().put("source", pdfPath.toAbsolutePath().toString());
                doc.getMetadata().put("fileType", "pdf");
                doc.getMetadata().put("page", page);

                docs.add(doc);
            }

            if (docs.isEmpty()) {
                warnings.add("No extractable text found in PDF (may be scanned image): " + pdfPath.toAbsolutePath());
            }
        } catch (Exception e) {
            warnings.add("Failed reading PDF " + pdfPath.toAbsolutePath() + ": " + e.getMessage());
        }

        return docs;
    }

    /**
     * Represents the result of document ingestion, including the number of
     * documents and chunks processed, and any warnings.
     *
     * @param documents Number of documents processed
     * @param chunks    Number of chunks created from documents
     * @param warnings  List of warnings encountered during ingestion
     */
    public record IngestResult(int documents, int chunks, List<String> warnings) {
    }
}