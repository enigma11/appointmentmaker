package com.gdiama;

import com.gdiama.infrastructure.AuditRepository;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document
public class Audit {

    @Field
    private String auditMessage;
    @Field
    private final Date startDate;
    @Field
    private Date endDate;
    @Field
    private Long duration;

    @Transient
    private StringBuilder auditMessagBuilder = new StringBuilder();
    @Transient
    private final AuditRepository auditRepository;

    public Audit(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
        this.startDate = new Date();
    }

    public void append(String message) {
        auditMessagBuilder.append(message).append("\n");
    }

    public String getAuditMessage() {
        auditMessage = auditMessagBuilder.toString();
        return auditMessage;
    }

    public void save() {
        getAuditMessage();
        endDate = new Date();
        duration = endDate.getTime() - startDate.getTime();
        auditRepository.save(this);
    }
}
