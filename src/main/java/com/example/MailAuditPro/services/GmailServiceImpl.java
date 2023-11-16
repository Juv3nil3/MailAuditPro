package com.example.MailAuditPro.services;

import com.example.MailAuditPro.exceptions.GmailServiceFetchException;
import com.example.MailAuditPro.exceptions.MessageNotFoundException;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GmailServiceImpl implements GmailService{

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    @Autowired
    private Gmail gmail;

    @Value("${gmail.userId}")
    private String userId;

    @Override
    public List<Message> fetchMessagesBySubject(String subject) throws MessageNotFoundException, GmailServiceFetchException {
        String query = String.format("subject:%s", subject);
        return fetchMessagesByQuery(query);
    }

    @Override
    public List<Message> fetchMessagesBySubjectAndStartDate(String subject, Date startDate) throws MessageNotFoundException, GmailServiceFetchException {
        String formattedStartDate = dateFormat.format(startDate);
        String query = String.format("subject:%s after:%s", subject, formattedStartDate);
        return fetchMessagesByQuery(query);
    }

    @Override
    public List<Message> fetchMessagesBySubjectAndEndDate(String subject, Date endDate) throws MessageNotFoundException, GmailServiceFetchException {
        String formattedEndDate = dateFormat.format(endDate);
        String query = String.format("subject:%s before:%s", subject, formattedEndDate);
        return fetchMessagesByQuery(query);
    }

    @Override
    public List<Message> fetchMessagesBySubjectAndDateRange(String subject, Date startDate, Date endDate) throws MessageNotFoundException, GmailServiceFetchException {
        String formattedStartDate = dateFormat.format(startDate);
        String formattedEndDate = dateFormat.format(endDate);
        String query = String.format("subject:%s after:%s before:%s", subject, formattedStartDate, formattedEndDate);
        return fetchMessagesByQuery(query);
    }

    @Override
    public List<String> fetchAttachmentIds(String messageId) throws MessageNotFoundException, GmailServiceFetchException {
        Message message = fetchMessageById(messageId);
        List<MessagePart> attachmentParts = extractAttachmentPartsFromMessage(message);

        return attachmentParts.stream()
                .map(part -> part.getBody().getAttachmentId())
                .collect(Collectors.toList());
    }

    @Override
    public byte[] fetchPdfAttachment(String messageId, String attachmentId) throws GmailServiceFetchException {
        try {
            return gmail.users().messages().attachments().get(userId, messageId, attachmentId).execute().decodeData();
        } catch (IOException e) {
            String errorMessage = String.format("Error while fetching PDF attachment: %s", e.getMessage());
            throw new GmailServiceFetchException(errorMessage, e);
        }
    }

    /**
     * Fetch a Gmail message by its ID.
     *
     * @param messageId the ID of the Gmail message to fetch
     * @return the Gmail message with the specified ID
     * @throws MessageNotFoundException
     * @throws GmailServiceFetchException
     */
    private Message fetchMessageById(String messageId) throws MessageNotFoundException, GmailServiceFetchException {
        try {
            Message message = gmail.users().messages().get(userId, messageId).execute();
            if (message == null) {
                String errorMessage = String.format("Message not found for ID: %s", messageId);
                throw new MessageNotFoundException(errorMessage);
            }
            return message;
        } catch (IOException e) {
            String errorMessage = String.format("Error while fetching message by ID: %s", messageId);
            throw new GmailServiceFetchException(errorMessage, e);
        }
    }


    /**
     * Fetch Gmail messages by a query.
     *
     * @param query the query string used to filter messages
     * @return a list of Gmail messages matching the query
     * @throws MessageNotFoundException
     * @throws GmailServiceFetchException
     */
    private List<Message> fetchMessagesByQuery(String query) throws MessageNotFoundException, GmailServiceFetchException {
        try {
            List<Message> messages = gmail.users().messages().list(userId).setQ(query).execute().getMessages();
            if (messages == null) {
                String errorMessage = String.format("No messages found by query: %s", query);
                throw new MessageNotFoundException(errorMessage);
            }
            return messages;
        } catch (IOException e) {
            String errorMessage = String.format("Error while fetching messages by query: %s", query);
            throw new GmailServiceFetchException(errorMessage, e);
        }
    }

    /**
     * Extract attachment parts from a Gmail message.
     *
     * @param message the Gmail message from which to extract attachment parts
     * @return a list of message parts representing attachments
     */
    private List<MessagePart> extractAttachmentPartsFromMessage(Message message) {
        return message.getPayload().getParts()
                .stream()
                .filter(this::isAttachment)
                .collect(Collectors.toList());
    }

    /**
     * Check if a message part represents an attachment.
     *
     * @param part the message part to check
     * @return true if the part is an attachment, false otherwise
     */
    private boolean isAttachment(MessagePart part) {
        return part.getFilename() != null && part.getBody() != null;
    }
}
