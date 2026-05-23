package com.smartwallet.service;

import com.smartwallet.dto.TransactionRequest;
import com.smartwallet.exception.BadRequestException;
import com.smartwallet.exception.ResourceNotFoundException;
import com.smartwallet.model.Transaction;
import com.smartwallet.model.TransactionStatus;
import com.smartwallet.model.User;
import com.smartwallet.model.Wallet;
import com.smartwallet.repository.TransactionRepository;
import com.smartwallet.repository.UserRepository;
import com.smartwallet.repository.WalletRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(UserRepository userRepository,
                              WalletRepository walletRepository,
                              TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public String sendMoney(TransactionRequest request) {

        if (request.getAmount() == null ||
                request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount must be greater than zero");
        }

        String senderEmail = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User sender = userRepository.findByEmail(senderEmail);

        if (sender == null) {
            throw new ResourceNotFoundException("Sender not found");
        }

        User receiver = userRepository.findByEmail(request.getReceiverEmail());

        if (receiver == null) {
            throw new ResourceNotFoundException("Receiver not found");
        }

        if (sender.getEmail().equals(receiver.getEmail())) {
            throw new BadRequestException("Cannot send money to yourself");
        }

        Wallet senderWallet = walletRepository.findByUserEmail(sender.getEmail());
        Wallet receiverWallet = walletRepository.findByUserEmail(receiver.getEmail());

        if (senderWallet == null) {
            throw new ResourceNotFoundException("Sender wallet not found");
        }

        if (receiverWallet == null) {
            throw new ResourceNotFoundException("Receiver wallet not found");
        }

        if (senderWallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new BadRequestException("Insufficient balance");
        }

        Transaction transaction = new Transaction(
                sender.getEmail(),
                receiver.getEmail(),
                request.getAmount(),
                TransactionStatus.PENDING,
                LocalDateTime.now()
        );

        transactionRepository.save(transaction);

        senderWallet.setBalance(
                senderWallet.getBalance().subtract(request.getAmount())
        );

        receiverWallet.setBalance(
                receiverWallet.getBalance().add(request.getAmount())
        );

        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        transaction.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(transaction);

        return "Transaction Successful";
    }

    public Page<Transaction> getTransactionHistory(String email, int page, int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("timestamp").descending()
        );

        return transactionRepository.findBySenderEmailOrReceiverEmail(
                email,
                email,
                pageable
        );
    }

    public Page<Transaction> getSentTransactions(String email, int page, int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("timestamp").descending()
        );

        return transactionRepository.findBySenderEmail(email, pageable);
    }

    public Page<Transaction> getReceivedTransactions(String email, int page, int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("timestamp").descending()
        );

        return transactionRepository.findByReceiverEmail(email, pageable);
    }
}