package com.example.MailAuditPro.services;

import com.example.MailAuditPro.exceptions.PdfParsingException;
import com.example.MailAuditPro.model.PdfContent;
import com.example.MailAuditPro.repository.PdfRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PdfParsingServiceImplTest {

    @Mock
    private GmailService gmailService;

    @Mock
    private PdfRepository pdfRepository;

    @InjectMocks
    private PdfParsingServiceImpl pdfParsingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void extractPdfContentFromAttachment_Success() throws PdfParsingException, IOException {
        // Arrange

        // Give the path of the local pdf to test the function
        Path pdfPath = Path.of("src/main/resources/RedHat Certified.pdf");
        byte[] pdfBytes = Files.readAllBytes(pdfPath);
        String messageId = "123";
        String attachmentId = "attachment1";

        // Mock PDFTextStripper
        PDFTextStripper textStripperMock = mock(PDFTextStripper.class);

        // Act
        PdfContent result = pdfParsingService.extractPdfContentFromAttachment(pdfBytes, messageId, attachmentId);

        // Assert
        assertNotNull(result);
        assertTrue(result.getTextContent().contains("Red Hat"), "Actual text content: " + result.getTextContent());
        assertEquals(4, result.getPdfImages().size()); //There are 2 images in the pdf

        // Verify that save method was called on the PdfRepository
        verify(pdfRepository, times(1)).save(any(PdfContent.class));
    }

}