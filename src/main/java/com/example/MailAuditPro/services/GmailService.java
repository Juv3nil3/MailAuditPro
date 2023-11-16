package com.example.MailAuditPro.services;

import com.example.MailAuditPro.exceptions.GmailServiceFetchException;
import com.example.MailAuditPro.exceptions.MessageNotFoundException;
import com.google.api.services.gmail.model.Message;

import java.util.Date;
import java.util.List;

public interface GmailService {
    /**
     * Fetch messages by subject.
     *
     * @param subject the subject of the messages to fetch
     * @return a list of messages matching the subject
     * @throws MessageNotFoundException
     * @throws GmailServiceFetchException
     */
    List<Message> fetchMessagesBySubject(String subject) throws MessageNotFoundException, GmailServiceFetchException;

    /**
     * Fetch messages by subject and start date.
     *
     * @param subject the subject of the messages to fetch
     * @param startDate the start date for filtering messages
     * @return a list of messages matching the subject and start date
     * @throws MessageNotFoundException
     * @throws GmailServiceFetchException
     */
    List<Message> fetchMessagesBySubjectAndStartDate(String subject, Date startDate) throws MessageNotFoundException, GmailServiceFetchException;

    /**
     * Fetch messages by subject and end date.
     *
     * @param subject the subject of the messages to fetch
     * @param endDate the end date for filtering messages
     * @return a list of messages matching the subject and end date
     * @throws MessageNotFoundException
     * @throws GmailServiceFetchException
     */
    List<Message> fetchMessagesBySubjectAndEndDate(String subject, Date endDate) throws MessageNotFoundException, GmailServiceFetchException;

    /**
     * Fetch messages by subject and date range.
     *
     * @param subject the subject of the messages to fetch
     * @param startDate the start date for filtering messages
     * @param endDate the end date for filtering messages
     * @return a list of messages matching the subject and date range
     * @throws MessageNotFoundException
     * @throws GmailServiceFetchException
     */
    List<Message> fetchMessagesBySubjectAndDateRange(String subject, Date startDate, Date endDate) throws MessageNotFoundException, GmailServiceFetchException;

    /**
     * Fetch attachment IDs from a Gmail message.
     *
     * @param messageId the ID of the Gmail message
     * @return a list of attachment IDs found in the message
     * @throws MessageNotFoundException
     * @throws GmailServiceFetchException
     */
    List<String> fetchAttachmentIds(String messageId) throws MessageNotFoundException, GmailServiceFetchException;

    /**
     * Fetch the content of a PDF attachment from a Gmail message.
     *
     * @param messageId the ID of the Gmail message
     * @param attachmentId the ID of the attachment within the message
     * @return the byte array containing the PDF attachment content
     * @throws GmailServiceFetchException
     */
    byte[] fetchPdfAttachment(String messageId, String attachmentId) throws GmailServiceFetchException;

}
