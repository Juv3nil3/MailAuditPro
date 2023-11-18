package com.example.MailAuditPro.exceptions;

public class PdfParsingException extends Exception{
    public PdfParsingException(String message) {
        super(message);
    }

    public PdfParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
