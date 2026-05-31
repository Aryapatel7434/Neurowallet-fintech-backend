package com.smartwallet.controller;

import com.smartwallet.model.AuditLog;
import com.smartwallet.repository.AuditLogRepository;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private final AuditLogRepository auditLogRepository;

    public AuditController(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping
    public List<AuditLog> getAllAuditLogs() {

        System.out.println("Audit API called");

        return auditLogRepository.findAll();
    }
}