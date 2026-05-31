package com.smartwallet.service;

import com.smartwallet.model.AuditLog;
import com.smartwallet.repository.AuditLogRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service//Tells spring create AuditService object automatically in spring bean
//Track every important action performed in the system
public class AuditService {

    private final AuditLogRepository repository;

    public AuditService(
            AuditLogRepository repository) {

        this.repository = repository;
    }
       //save audit Record
    public void log(
            String email,
            String action,
            String status) {
        //create Audit object
        AuditLog log =
                new AuditLog(
                        email,
                        action,
                        status,
                        LocalDateTime.now()
                );

        repository.save(log);
    }
}