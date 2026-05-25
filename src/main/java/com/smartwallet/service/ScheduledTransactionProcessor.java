package com.smartwallet.service;

import com.smartwallet.model.ScheduledTransaction;
import com.smartwallet.model.TransactionStatus;
import com.smartwallet.model.Wallet;
import com.smartwallet.repository.ScheduledTransactionRepository;
import com.smartwallet.repository.WalletRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScheduledTransactionProcessor {

    private final ScheduledTransactionRepository repository;
    private final WalletRepository walletRepository;

    public ScheduledTransactionProcessor(
            ScheduledTransactionRepository repository,
            WalletRepository walletRepository) {

        this.repository = repository;
        this.walletRepository = walletRepository;
    }

    @Scheduled(fixedRate = 30000)
    @Transactional
    public void processScheduledTransactions() {

        List<ScheduledTransaction> transactions =
                repository.findByStatusAndScheduledTimeBefore(
                        TransactionStatus.PENDING,
                        LocalDateTime.now()
                );

        for (ScheduledTransaction tx : transactions) {

            Wallet senderWallet =
                    walletRepository.findByUserEmail(tx.getSenderEmail());

            Wallet receiverWallet =
                    walletRepository.findByUserEmail(tx.getReceiverEmail());

            if (senderWallet == null || receiverWallet == null) {
                tx.setStatus(TransactionStatus.FAILED);
                repository.save(tx);
                continue;
            }

            if (senderWallet.getBalance()
                    .compareTo(tx.getAmount()) >= 0) {

                senderWallet.setBalance(
                        senderWallet.getBalance()
                                .subtract(tx.getAmount())
                );

                receiverWallet.setBalance(
                        receiverWallet.getBalance()
                                .add(tx.getAmount())
                );

                walletRepository.save(senderWallet);
                walletRepository.save(receiverWallet);

                tx.setStatus(TransactionStatus.SUCCESS);

            } else {
                tx.setStatus(TransactionStatus.FAILED);
            }

            repository.save(tx);
        }
    }
}