package com.example.MailAuditPro.repository;

import com.example.MailAuditPro.model.PdfImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PdfImageRepository extends JpaRepository<PdfImage, Long> {
    boolean existsByMessageIdAndAttachmentIdAndImageIndex(String messageId, String attachmentId, int imageIndex);
}
