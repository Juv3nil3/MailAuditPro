package com.example.MailAuditPro.services;

import com.example.MailAuditPro.exceptions.PdfParsingException;
import com.example.MailAuditPro.model.PdfContent;
import com.example.MailAuditPro.repository.PdfRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PdfParsingServiceImplTest {

    @Mock
    private GmailService gmailService;

    @Mock
    private PdfRepository pdfRepository;

    @InjectMocks
    @Spy
    private PdfParsingServiceImpl pdfParsingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void extractPdfContentFromAttachment_Success() throws PdfParsingException, IOException {
        // Arrange

        Path pdfPath = Path.of("src/main/resources/RedHat Certified.pdf");
        byte[] pdfBytes = Files.readAllBytes(pdfPath);
        String messageId = "123";
        String attachmentId = "attachment1";


        try (InputStream inputStream = new ByteArrayInputStream(pdfBytes);
             PDDocument documentMock = PDDocument.load(inputStream)) {
            //System.out.println(password);
            PDFTextStripper textStripperMock = mock(PDFTextStripper.class);
            when(textStripperMock.getText(documentMock)).thenReturn("Arunima");

            // Act
            PdfContent result = pdfParsingService.extractPdfContentFromAttachment(pdfBytes, messageId, attachmentId);

            // Assert
            assertNotNull(result);
            assertTrue(result.getTextContent().contains("Arunima"));
            assertEquals(4, result.getPdfImages().size()); // Assuming there is one image
        } catch (InvalidPasswordException e) {
            // Handle the case where the password is incorrect
            fail("Incorrect password provided during PDF decryption.");
        }
    }


}