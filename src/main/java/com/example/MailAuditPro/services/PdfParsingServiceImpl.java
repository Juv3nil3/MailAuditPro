package com.example.MailAuditPro.services;

import com.example.MailAuditPro.exceptions.GmailServiceFetchException;
import com.example.MailAuditPro.exceptions.MessageNotFoundException;
import com.example.MailAuditPro.exceptions.PdfParsingException;
import com.example.MailAuditPro.model.PdfContent;
import com.example.MailAuditPro.model.PdfImage;
import com.example.MailAuditPro.repository.PdfRepository;
import com.google.api.services.gmail.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DeflaterOutputStream;

@Service
@Slf4j
public class PdfParsingServiceImpl implements PdfParserService {

    private final PDFTextStripper textStripper;

    public PdfParsingServiceImpl(PDFTextStripper textStripper) {
        this.textStripper = textStripper;
    }
    @Autowired
    private GmailService gmailService;

    @Autowired
    private PdfRepository pdfRepository;

    @Override
    public PdfContent extractPdfContentFromAttachment(byte[] pdfBytes, String messageId, String attachmentId)
            throws PdfParsingException {

        PdfContent pdfContent = new PdfContent();
        String password = "password";
        try {
            PDDocument document = PDDocument.load(pdfBytes, password);
            // Extract text from the PDF
            PDFTextStripper textStripper = new PDFTextStripper();
            String pdfText = textStripper.getText(document);
            pdfContent.setTextContent(pdfText);

            // Extract images from the PDF
            PDPageTree pages = document.getPages();
            int imageIndex = 1;

            for (PDPage page : pages) {
                PDResources resources = page.getResources();
                Iterable<COSName> xObjectNames = resources.getXObjectNames();

                for (COSName xObjectName : xObjectNames) {
                    if (resources.isImageXObject(xObjectName)) {
                        PDImageXObject image = (PDImageXObject) resources.getXObject(xObjectName);

                        // Convert the image to a buffered image
                        BufferedImage bufferedImage = image.getImage();

                        // Convert the buffered image to a byte array
                        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                             DeflaterOutputStream dos = new DeflaterOutputStream(baos)) {
                            // Convert BufferedImage to PNG format
                            ImageIO.write(bufferedImage, "PNG", dos);
                            dos.finish();
                            byte[] compressedImageContent = baos.toByteArray();
                            System.out.println(compressedImageContent.length);

                            // Save the PdfImage entity to the repository
                            PdfImage pdfImage = new PdfImage();
                            pdfImage.setMessageId(messageId);
                            pdfImage.setAttachmentId(attachmentId);
                            pdfImage.setImageIndex(imageIndex);
                            pdfImage.setImageContent(compressedImageContent);


                            // Add PdfImage to PdfContent
                            pdfContent.addPdfImage(pdfImage);

                            imageIndex++;
                        } catch (IOException e) {
                            // Handle exception when compressing or writing compressed image to byte array
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException e) {
            String errorMessage = String.format("Error during PDF processing for message ID: %s, attachment ID: %s",
                    messageId, attachmentId);
            throw new PdfParsingException(errorMessage, e);
        }

        // Save the PdfContent to the repository (consider moving this outside the function)
        pdfRepository.save(pdfContent);

        return pdfContent;
    }



    @Scheduled(fixedDelay = 60000)
    @Override
    public void extractPdfContentsFromAttachments() throws MessageNotFoundException, GmailServiceFetchException, PdfParsingException {
        log.info("Scheduled task started: extractPdfContentsFromAttachments");

        List<Message> messages = gmailService.fetchMessagesBySubject("Bank Account Statement");

        // Iterate through the fetched messages
        for (Message message : messages) {
            // Check if the message has already been parsed
            if (!pdfRepository.existsByMessageId(message.getId())) {
                List<String> attachmentIds = gmailService.fetchAttachmentIds(message.getId());

                // Process each attachment
                for (String attachmentId : attachmentIds) {
                    if (attachmentId != null) {
                        byte[] pdfBytes = gmailService.fetchPdfAttachment(message.getId(), attachmentId);

                        // Extract and save pdfContent
                        PdfContent pdfContent = extractPdfContentFromAttachment(pdfBytes, message.getId(), attachmentId);

                        // Set message id for tracking
                        pdfContent.setMessageId(message.getId());

                        // Save the pdfContent to the repository
                        pdfRepository.save(pdfContent);
                    }
                }
            }
        }
    }
}
