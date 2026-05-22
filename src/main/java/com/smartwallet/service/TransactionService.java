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
                request.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {

            throw new BadRequestException(
                    "Amount must be greater than zero"
            );
        }

        String senderEmail = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User sender =
                userRepository.findByEmail(senderEmail);

        User receiver =
                userRepository.findByEmail(
                        request.getReceiverEmail()
                );

        if (receiver == null) {
            throw new ResourceNotFoundException(
                    "Receiver not found"
            );
        }

        if (sender.getEmail().equals(receiver.getEmail())) {

            throw new BadRequestException(
                    "Cannot send money to yourself"
            );
        }
       //Check the Wallet
        Wallet senderWallet =
                walletRepository.findByUserEmail(
                        sender.getEmail()
                );

        Wallet receiverWallet =
                walletRepository.findByUserEmail(
                        receiver.getEmail()
                );

        if (senderWallet.getBalance()
                .compareTo(request.getAmount()) < 0) {

            throw new BadRequestException(
                    "Insufficient balance"
            );
        }

        // deduct sender balance
        senderWallet.setBalance(
                senderWallet.getBalance()
                        .subtract(request.getAmount())
        );

        // add receiver balance
        receiverWallet.setBalance(
                receiverWallet.getBalance()
                        .add(request.getAmount())
        );

        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        Transaction transaction = new Transaction(
                sender.getEmail(),
                receiver.getEmail(),
                request.getAmount(),
                "SUCCESS",
                LocalDateTime.now()
        );

        transactionRepository.save(transaction);

        return "Transaction Successful";
    }

    public List<Transaction>
    getTransactionHistory(String email) {

        return transactionRepository
                .findBySenderEmailOrReceiverEmail(
                        email,
                        email
                );
    }
}