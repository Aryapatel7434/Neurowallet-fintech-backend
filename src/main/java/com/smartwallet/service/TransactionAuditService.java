package com.smartwallet.service;

import com.smartwallet.model.Transaction;
import com.smartwallet.model.TransactionCategory;
import com.smartwallet.model.TransactionStatus;
import com.smartwallet.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionAuditService {

    private final TransactionRepository transactionRepository;

    public TransactionAuditService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveFailedTransaction(
            String senderEmail,
            String receiverEmail,
            BigDecimal amount) {

        Transaction failedTransaction = new Transaction(
                senderEmail,
                receiverEmail,
                amount,
                TransactionStatus.FAILED,
                TransactionCategory.OTHER,
                LocalDateTime.now()
        );

        transactionRepository.save(failedTransaction);
    }
}