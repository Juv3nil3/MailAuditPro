package com.example.MailAuditPro.controller;

import com.example.MailAuditPro.exceptions.GmailServiceFetchException;
import com.example.MailAuditPro.exceptions.MessageNotFoundException;
import com.example.MailAuditPro.exceptions.PdfParsingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(PdfParsingException.class)
    public ResponseEntity<String> handlePdfParsingException(PdfParsingException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(GmailServiceFetchException.class)
    public ResponseEntity<String> handleGmailServiceFetchException(GmailServiceFetchException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MessageNotFoundException.class)
    public ResponseEntity<String> handleMessageNotFoundException(MessageNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
}
