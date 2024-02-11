package com.example.MailAuditPro.controller;

import com.example.MailAuditPro.exceptions.GmailServiceFetchException;
import com.example.MailAuditPro.exceptions.MessageNotFoundException;
import com.example.MailAuditPro.exceptions.PdfParsingException;
import com.example.MailAuditPro.services.GmailService;
import com.example.MailAuditPro.services.PdfParserService;
import com.google.api.services.gmail.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PdfParserController {

    @Autowired
    private GmailService gmailService;

    @Autowired
    private PdfParserService pdfParserService;

    @GetMapping("/parse-bank-statements")
    public ResponseEntity<List<String>> parseBankStatements(
            @RequestParam("subject") String subject) throws MessageNotFoundException, GmailServiceFetchException, PdfParsingException {
        List<Message> messages;

        // messages with subject
        messages = gmailService.fetchMessagesBySubject(subject);

        List<String> responses = pdfParserService.extractPdfContentsFromAttachments(messages);

        if (responses.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(responses);
    }

}
