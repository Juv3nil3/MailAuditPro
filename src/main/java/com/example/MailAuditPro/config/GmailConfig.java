package com.example.MailAuditPro.config;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;

@Configuration
public class GmailConfig {

    private JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Value("${gmail.applicationName}")
    private String applicationName;

    @Value("${gmail.tokensDirectoryPath}")
    private String tokensDirectoryPath;

    @Value("${gmail.credentialsFilePath}")
    private String credentialsFilePath;

    @Value("${gmail.userId}")
    private String userId;

    private JsonFactory JSON_FACTORY() {
        return GsonFactory.getDefaultInstance();
    }

    @Bean
    public Gmail gmail() throws Exception {
        try{
            // initialize the Gmail API client
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Credential credentials = getCredentials(HTTP_TRANSPORT);
            Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credentials)
                    .setApplicationName(applicationName)
                    .build();
            return service;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        try{
            // load client secrets from the provided gmail-credentials.json file
            InputStream in = getClass().getResourceAsStream(credentialsFilePath);
            if (in == null) {
                throw new IOException("Resource not found: " + credentialsFilePath);
            }
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

            // set up the OAuth 2.0 flow for Gmail API access
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, Collections.singletonList(GmailScopes.GMAIL_READONLY))
                    .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(tokensDirectoryPath)))
                    .setAccessType("offline")
                    .build();
            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();

            // authorize the application for the specified user
            return new AuthorizationCodeInstalledApp(flow, receiver).authorize(userId);
        } catch (Exception e) {
            e.printStackTrace();
            throw  e;
        }
    }

}
