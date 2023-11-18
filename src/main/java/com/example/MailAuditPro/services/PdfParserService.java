package com.example.MailAuditPro.services;

import com.example.MailAuditPro.exceptions.GmailServiceFetchException;
import com.example.MailAuditPro.exceptions.MessageNotFoundException;
import com.example.MailAuditPro.exceptions.PdfParsingException;
import com.example.MailAuditPro.model.PdfContent;
import com.google.api.services.gmail.model.Message;

import java.util.List;

public interface PdfParserService {

    /**
     * Extracts text and images from a PDF attachment.
     *
     * @param pdfBytes the content of the PDF attachment
     * @param messageId the ID of the Gmail message
     * @param attachmentId the ID of the attachment within the message
     * @return a {@link PdfContent} object containing extracted text and image URLs
     * @throws PdfParsingException
     */
    PdfContent extractPdfContentFromAttachment(byte[] pdfBytes, String messageId, String attachmentId) throws PdfParsingException;


    /**
     * Extracts PDF content from a list of Gmail messages.
     * @return a list of {@link PdfContent} objects containing extracted text and image URLs
     * @throws MessageNotFoundException
     * @throws GmailServiceFetchException
     * @throws PdfParsingException
     */
    void extractPdfContentsFromAttachments() throws MessageNotFoundException, GmailServiceFetchException, PdfParsingException;

}
