package com.example.MailAuditPro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PdfContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "message_id")
    private String messageId;

    @Column(name = "text_content")
    private String textContent;

    @OneToMany(mappedBy = "pdfContent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PdfImage> pdfImages = new ArrayList<>();

    public void addPdfImage(PdfImage pdfImage) {
        pdfImages.add(pdfImage);
        pdfImage.setPdfContent(this);
    }

}
