package com.smartwallet.service;

import com.smartwallet.dto.TransactionRequest;
import com.smartwallet.exception.BadRequestException;
import com.smartwallet.exception.ResourceNotFoundException;
import com.smartwallet.model.Transaction;
import com.smartwallet.model.User;
import com.smartwallet.model.Wallet;
import com.smartwallet.repository.TransactionRepository;
import com.smartwallet.repository.UserRepository;
import com.smartwallet.repository.WalletRepository;
import java.time.LocalDateTime;
import java.util.List;
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

        User sender = userRepository.findByEmail(request.getSenderEmail());
        User receiver = userRepository.findByEmail(request.getReceiverEmail());

        if (sender == null || receiver == null) {
            throw new ResourceNotFoundException("Sender or Receiver not found");
        }

        Wallet senderWallet;
        senderWallet = walletRepository.findByUserUserId(sender.getUserId());

        Wallet receiverWallet =
                walletRepository.findByUserUserId(receiver.getUserId());

        if (senderWallet == null || receiverWallet == null) {
            throw new ResourceNotFoundException("Sender or Receiver wallet not found");
        }

        if (senderWallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new BadRequestException("Insufficient balance");
        }

        senderWallet.setBalance(
                senderWallet.getBalance().subtract(request.getAmount())
        );

        receiverWallet.setBalance(
                receiverWallet.getBalance().add(request.getAmount())
        );

        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        Transaction transaction = new Transaction(
                request.getSenderEmail(),
                request.getReceiverEmail(),
                request.getAmount(),
                "SUCCESS",
                LocalDateTime.now()
        );

        transactionRepository.save(transaction);

        return "Transaction Successful";
    }

    public List<Transaction> getTransactionHistory(String email) {
        return transactionRepository.findBySenderEmailOrReceiverEmail(email, email);
    }
}