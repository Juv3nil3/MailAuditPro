package com.example.MailAuditPro.repository;

import com.example.MailAuditPro.model.PdfContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PdfRepository extends JpaRepository<PdfContent,Integer> {
    boolean existsByMessageId(String messageId);
}
