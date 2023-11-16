package com.example.MailAuditPro.exceptions;

public class GmailServiceFetchException extends Exception{
    public GmailServiceFetchException(String message) {
        super(message);
    }

    public GmailServiceFetchException(String message, Throwable cause) {
        super(message, cause);
    }

}
