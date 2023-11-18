package com.example.MailAuditPro.services;

import com.example.MailAuditPro.exceptions.GmailServiceFetchException;
import com.example.MailAuditPro.exceptions.MessageNotFoundException;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GmailServiceTest {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private Gmail gmail;

    @Autowired
    private GmailService gmailService;

    @ParameterizedTest
    @CsvSource({
            "Bank statement, true",
            "Invalid subject, false"
    })
    void fetchMessagesBySubject(String subject, boolean expectedResult) throws MessageNotFoundException, GmailServiceFetchException {
        if (expectedResult) {
            List<Message> messages = gmailService.fetchMessagesBySubject(subject);
            assertNotNull(messages);
            assertFalse(messages.isEmpty());
        } else {
            assertThrows(MessageNotFoundException.class, () -> gmailService.fetchMessagesBySubject(subject));
        }
    }

    @ParameterizedTest
    @CsvSource({
            "Bank statement, 2023-11-01, true",
            "Invalid subject, 2023-11-01, false"
    })
    void fetchMessagesBySubjectAndStartDate(String subject, String startDateStr, boolean expectedResult) throws MessageNotFoundException, GmailServiceFetchException, ParseException {
        Date startDate = dateFormat.parse(startDateStr);

        if (expectedResult) {
            List<Message> messages = gmailService.fetchMessagesBySubjectAndStartDate(subject, startDate);
            assertNotNull(messages);
            assertFalse(messages.isEmpty());
        } else {
            assertThrows(MessageNotFoundException.class, () -> gmailService.fetchMessagesBySubjectAndStartDate(subject, startDate));
        }
    }

    @ParameterizedTest
    @CsvSource({
            "Bank statement, 2023-11-01, true",
            "Invalid subject, 2023-11-01, false"
    })
    void fetchMessagesBySubjectAndEndDate(String subject, String endDateStr, boolean expectedResult) throws MessageNotFoundException, GmailServiceFetchException, ParseException{
        Date endDate = dateFormat.parse(endDateStr);

        if (expectedResult) {
            List<Message> messages = gmailService.fetchMessagesBySubjectAndEndDate(subject, endDate);
            assertNotNull(messages);
            assertFalse(messages.isEmpty());
        } else {
            assertThrows(MessageNotFoundException.class, () -> gmailService.fetchMessagesBySubjectAndEndDate(subject, endDate));
        }
    }

    @ParameterizedTest
    @CsvSource({
            "Bank Account statement, 2023-11-01, 2023-11-11, true",
            "Invalid subject, 2023-11-01, 2023-11-11, false"
    })
    void fetchMessagesBySubjectAndDateRange(String subject, String startDateStr, String endDateStr, boolean expectedResult) throws MessageNotFoundException, GmailServiceFetchException, ParseException {
        Date startDate = dateFormat.parse(startDateStr);
        Date endDate = dateFormat.parse(endDateStr);

        if (expectedResult) {
            List<Message> messages = gmailService.fetchMessagesBySubjectAndDateRange(subject, startDate, endDate);
            assertNotNull(messages);
            assertFalse(messages.isEmpty());
        } else {
            assertThrows(MessageNotFoundException.class, () -> gmailService.fetchMessagesBySubjectAndDateRange(subject, startDate, endDate));
        }
    }

    @ParameterizedTest
    @CsvSource({
            "Bank Account Statement, true"
    })
    void testFetchPdfAttachment(String subject, boolean expectedResult) throws MessageNotFoundException, GmailServiceFetchException {
        if (expectedResult) {
            System.out.println("Fetching messages for subject: " + subject);
            List<Message> messages = gmailService.fetchMessagesBySubject(subject);
            assertNotNull(messages);
            assertFalse(messages.isEmpty());

            for (Message message : messages) {
                List<String> attachmentIds = gmailService.fetchAttachmentIds(message.getId());
                assertNotNull(attachmentIds);
                assertFalse(attachmentIds.isEmpty());

                for (String attachmentId : attachmentIds) {
                    if (attachmentId != null) {
                        byte[] pdfBytes = gmailService.fetchPdfAttachment(message.getId(), attachmentId);
                        assertTrue(isValidPdf(pdfBytes));
                    }
                }
            }
        } else {
            assertThrows(MessageNotFoundException.class, () -> gmailService.fetchMessagesBySubject(subject));
        }

    }

    private boolean isValidPdf(byte[] data) {
        try {
            //Add the password here if pdf is encrypted
            String password = "password";
            PDDocument.load(data, password).close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}