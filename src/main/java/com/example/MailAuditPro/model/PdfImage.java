package com.example.MailAuditPro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PdfImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pdf_content_id")
    private PdfContent pdfContent;

    @Column(name = "message_id")
    private String messageId;

    @Column(name = "attachment_id", columnDefinition = "LONGTEXT")
    private String attachmentId;

    @Column(name = "image_index")
    private int imageIndex;

    @Lob
    @Column(name = "image_content", columnDefinition = "LONGBLOB")
    private byte[] imageContent;

}
