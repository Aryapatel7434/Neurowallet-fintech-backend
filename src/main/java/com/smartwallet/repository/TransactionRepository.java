package com.smartwallet.repository;

import com.smartwallet.model.Transaction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository
        extends JpaRepository<Transaction,Long> {
    
    List<Transaction>findBySenderEmailOrReceiverEmail(String senderEmail,String receiverEmail);
}