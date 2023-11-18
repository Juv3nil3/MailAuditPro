package com.example.MailAuditPro.services;

import com.example.MailAuditPro.exceptions.GmailServiceFetchException;
import com.example.MailAuditPro.exceptions.MessageNotFoundException;
import com.example.MailAuditPro.exceptions.PdfParsingException;
import com.example.MailAuditPro.model.PdfContent;
import com.example.MailAuditPro.model.PdfImage;
import com.example.MailAuditPro.repository.PdfRepository;
import com.google.api.services.gmail.model.Message;
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

@Service
public class PdfParsingServiceImpl implements PdfParserService {
    @Autowired
    private GmailService gmailService;

    @Autowired
    private PdfRepository pdfRepository;

    @Override
    public PdfContent extractPdfContentFromAttachment(byte[] pdfBytes, String messageId, String attachmentId)
            throws PdfParsingException {

        return null;
    }



    @Scheduled(fixedRate = 6000)
    @Override
    public void extractPdfContentsFromAttachments() throws MessageNotFoundException, GmailServiceFetchException, PdfParsingException {

        List<Message> messages = gmailService.fetchMessagesBySubject("Bank Statement");

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
