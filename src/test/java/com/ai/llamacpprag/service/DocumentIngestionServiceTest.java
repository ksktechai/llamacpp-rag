package com.ai.llamacpprag.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentIngestionServiceTest {

    @Mock
    private VectorStore vectorStore;

    @Mock
    private EmbeddingModel embeddingModel;

    private DocumentIngestionService service;

    @BeforeEach
    void setUp() {
        service = new DocumentIngestionService(vectorStore, embeddingModel);
    }

    @Test
    void ingestLocalFolder_FolderNotFound(@TempDir Path tempDir) throws IOException {
        Path nonExistent = tempDir.resolve("nothing");
        var result = service.ingestLocalFolder(nonExistent);

        assertEquals(0, result.documents());
        assertEquals(0, result.chunks());
        assertEquals(1, result.warnings().size());
        assertTrue(result.warnings().get(0).contains("Folder not found"));
    }

    @Test
    void ingestLocalFolder_EmptyFolder(@TempDir Path tempDir) throws IOException {
        var result = service.ingestLocalFolder(tempDir);

        assertEquals(0, result.documents());
        assertEquals(0, result.chunks());
        assertTrue(result.warnings().isEmpty());
    }

    @Test
    void ingestLocalFolder_TextAndMarkdown(@TempDir Path tempDir) throws IOException {
        Files.writeString(tempDir.resolve("test.txt"), "Hello World");
        Files.writeString(tempDir.resolve("README.md"), "# Title\n\nContent");
        Files.writeString(tempDir.resolve("ignore.me"), "Ignored");

        var result = service.ingestLocalFolder(tempDir);

        assertEquals(2, result.documents());
        // Simple splitter will likely produce 1 chunk per small doc
        // Mock VectorStore does nothing by default, which is fine
        verify(vectorStore, times(2)).add(anyList());
        assertTrue(result.warnings().isEmpty());
    }

    @Test
    void ingestLocalFolder_PdfProcessing(@TempDir Path tempDir) throws IOException {
        Path pdfPath = tempDir.resolve("test.pdf");
        createTestPdf(pdfPath, "PDF Content Test");

        var result = service.ingestLocalFolder(tempDir);

        // One page PDF -> 1 raw doc
        assertEquals(1, result.documents());
        verify(vectorStore, times(1)).add(anyList());
        assertTrue(result.warnings().isEmpty());
    }

    @Test
    void ingestLocalFolder_PdfEmptyOrScanned(@TempDir Path tempDir) throws IOException {
        // Create a PDF with no text (empty page)
        Path pdfPath = tempDir.resolve("empty.pdf");
        try (PDDocument doc = new PDDocument()) {
            doc.addPage(new PDPage());
            doc.save(pdfPath.toFile());
        }

        var result = service.ingestLocalFolder(tempDir);

        // Only warning
        assertEquals(0, result.documents());
        assertFalse(result.warnings().isEmpty());
        assertTrue(result.warnings().get(0).contains("No extractable text"));
    }

    @Test
    void ingestLocalFolder_CorruptPdf(@TempDir Path tempDir) throws IOException {
        // Create a file named .pdf but with garbage content
        Files.writeString(tempDir.resolve("fake.pdf"), "Not a PDF");

        var result = service.ingestLocalFolder(tempDir);

        assertEquals(0, result.documents());
        assertFalse(result.warnings().isEmpty());
        assertTrue(result.warnings().get(0).contains("Failed reading PDF"));
    }

    private void createTestPdf(Path path, String content) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {
                contents.beginText();
                contents.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                contents.newLineAtOffset(100, 700);
                contents.showText(content);
                contents.endText();
            }
            doc.save(path.toFile());
        }
    }

    @Test
    void ingestLocalFolder_EmptyTextFile(@TempDir Path tempDir) throws IOException {
        // Create an empty text file
        Files.writeString(tempDir.resolve("empty.txt"), "");
        // Create a blank text file (only whitespace)
        Files.writeString(tempDir.resolve("blank.md"), "   \n\t  ");

        var result = service.ingestLocalFolder(tempDir);

        // Empty/blank files should be skipped
        assertEquals(0, result.documents());
        assertEquals(0, result.chunks());
        assertTrue(result.warnings().isEmpty());
        verify(vectorStore, never()).add(anyList());
    }

    @Test
    void ingestLocalFolder_VectorStoreThrowsException(@TempDir Path tempDir) throws IOException {
        Files.writeString(tempDir.resolve("test.txt"), "Some valid content here");

        // Simulate VectorStore throwing an exception
        doThrow(new RuntimeException("Vector store error")).when(vectorStore).add(anyList());

        var result = service.ingestLocalFolder(tempDir);

        // Should have a warning about the failure
        // Note: rawDocs is NOT incremented because exception is caught before increment
        assertFalse(result.warnings().isEmpty());
        assertTrue(result.warnings().get(0).contains("Failed ingesting"));
    }

    @Test
    void ingestResult_RecordAccessors() {
        // Test the IngestResult record accessors
        List<String> warnings = List.of("warning1", "warning2");
        var result = new DocumentIngestionService.IngestResult(5, 10, warnings);

        assertEquals(5, result.documents());
        assertEquals(10, result.chunks());
        assertEquals(warnings, result.warnings());
        assertEquals(2, result.warnings().size());
    }
}
