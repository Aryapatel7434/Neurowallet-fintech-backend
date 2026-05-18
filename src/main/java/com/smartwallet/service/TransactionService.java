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

@Service
public class TransactionService {
     //Dependancy injection
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    //constructor injection
    public TransactionService(UserRepository userRepository,
                              WalletRepository walletRepository,
                              TransactionRepository transactionRepository) {

        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }
    //Main Business Method
    public String sendMoney(TransactionRequest request) {

        User sender =
                userRepository.findByEmail(request.getSenderEmail());

        User receiver =
                userRepository.findByEmail(request.getReceiverEmail());

        if (sender == null || receiver == null) {
           throw new ResourceNotFoundException("Sender or Receiver not found");
        }

        Wallet senderWallet =
                walletRepository.findByUserId(sender.getUserId());

        Wallet receiverWallet =
                walletRepository.findByUserId(receiver.getUserId());

        if (senderWallet.getBalance() < request.getAmount()) {
          throw new BadRequestException("Insufficient balance");
        }

        senderWallet.setBalance(
                senderWallet.getBalance() - request.getAmount());

        receiverWallet.setBalance(
                receiverWallet.getBalance() + request.getAmount());

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
