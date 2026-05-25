package com.smartwallet.repository;

import com.smartwallet.model.ScheduledTransaction;
import com.smartwallet.model.TransactionStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduledTransactionRepository
        extends JpaRepository<ScheduledTransaction, Long> {

    List<ScheduledTransaction> findByStatusAndScheduledTimeBefore(
            TransactionStatus status,
            LocalDateTime time
    );
}