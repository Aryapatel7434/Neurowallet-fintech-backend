package com.smartwallet.service;
import com.smartwallet.model.WalletTransaction;
import com.smartwallet.repository.WalletTransactionRepository;
import java.time.LocalDateTime;
import com.smartwallet.dto.AddMoneyRequest;
import com.smartwallet.dto.WithdrawRequest;
import com.smartwallet.exception.BadRequestException;
import com.smartwallet.exception.ResourceNotFoundException;
import com.smartwallet.model.Wallet;
import com.smartwallet.repository.UserRepository;
import com.smartwallet.repository.WalletRepository;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.smartwallet.model.WalletTransaction;
import java.util.List;
import com.smartwallet.dto.TransferMoneyRequest;
import com.smartwallet.model.Transaction;
import com.smartwallet.model.TransactionStatus;
import com.smartwallet.repository.TransactionRepository;

import java.time.LocalDateTime;
@Service
public class WalletService {

    private static final Logger logger =
            LoggerFactory.getLogger(WalletService.class);

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final WalletCacheService walletCacheService;
    private final WalletTransactionRepository walletTransactionRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationService notificationService;
    public WalletService(
            WalletRepository walletRepository,
            UserRepository userRepository,
            WalletCacheService walletCacheService,WalletTransactionRepository walletTransactionRepository,TransactionRepository transactionRepository,NotificationService notificationService) {

        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
        this.walletCacheService = walletCacheService;
        this.walletTransactionRepository=walletTransactionRepository;
        this.transactionRepository =transactionRepository;
        this.notificationService =notificationService;
    }

    @Cacheable(value = "myWallet", key = "#email")
    public Wallet getWalletByEmail(String email) {

        logger.info("Fetching wallet from MySQL for: {}", email);

        Wallet wallet = walletRepository.findByUserEmail(email);

        if (wallet == null) {

            logger.warn("Wallet not found for user: {}", email);

            throw new ResourceNotFoundException("Wallet not found");
        }

        logger.info("Wallet fetched successfully for user: {}", email);

        return wallet;
    }

    public Wallet getMyWallet() {

        String email = getCurrentUserEmail();

        logger.info("Get my wallet request received for user: {}", email);

        return getWalletByEmail(email);
    }

    @Transactional
    public Wallet addMoney(AddMoneyRequest request) {

        if (request.getAmount() == null ||
                request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {

            logger.warn("Add money failed due to invalid amount: {}",
                    request.getAmount());

            throw new BadRequestException("Amount must be greater than zero");
        }

        String email = getCurrentUserEmail();

        logger.info("Add money request received for user: {}, amount: {}",
                email,
                request.getAmount());

        Wallet wallet = walletRepository.findByUserEmail(email);

        if (wallet == null) {

            logger.warn("Add money failed. Wallet not found for user: {}", email);

            throw new ResourceNotFoundException("Wallet not found");
        }

        wallet.setBalance(
                wallet.getBalance().add(request.getAmount())
        );

        Wallet savedWallet = walletRepository.save(wallet);
        notificationService
        .createNotification(

                email,

                "₹" +
                request.getAmount() +
                " added to your wallet"

        );
        WalletTransaction transaction =
        new WalletTransaction();

transaction.setWallet(savedWallet);

transaction.setAmount(
        request.getAmount()
);

transaction.setType("CREDIT");

transaction.setCreatedAt(
        LocalDateTime.now()
);

walletTransactionRepository.save(
        transaction
);
        walletCacheService.clearWalletCache(email);

        logger.info("Money added successfully for user: {}, new balance: {}",
                email,
                savedWallet.getBalance());

        return savedWallet;
    }

   @Transactional
public Wallet withdrawMoney(WithdrawRequest request) {

    if (request.getAmount() == null ||
            request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {

        logger.warn("Withdraw failed due to invalid amount: {}",
                request.getAmount());

        throw new BadRequestException(
                "Withdraw amount must be greater than zero"
        );
    }

    String email = getCurrentUserEmail();

    logger.info(
            "Withdraw request received for user: {}, amount: {}",
            email,
            request.getAmount()
    );

    Wallet wallet =
            walletRepository.findByUserEmail(email);

    if (wallet == null) {

        logger.warn(
                "Withdraw failed. Wallet not found for user: {}",
                email
        );

        throw new ResourceNotFoundException(
                "Wallet not found"
        );
    }

    if (wallet.getBalance()
            .compareTo(request.getAmount()) < 0) {

        logger.warn(
                "Withdraw failed due to insufficient balance for user: {}",
                email
        );

        throw new BadRequestException(
                "Insufficient wallet balance"
        );
    }

    wallet.setBalance(
            wallet.getBalance()
                    .subtract(request.getAmount())
    );

    // SAVE DEBIT TRANSACTION
    WalletTransaction transaction =
            new WalletTransaction();

    transaction.setWallet(wallet);

    transaction.setAmount(
            request.getAmount()
    );

    transaction.setType(
            "DEBIT"
    );

    transaction.setCreatedAt(
            LocalDateTime.now()
    );

    walletTransactionRepository.save(
            transaction
    );

    Wallet savedWallet =
            walletRepository.save(wallet);
    notificationService
        .createNotification(

                email,

                "₹" +
                request.getAmount() +
                " withdrawn from your wallet"

        );

    walletCacheService.clearWalletCache(
            email
    );

    logger.info(
            "Money withdrawn successfully for user: {}, new balance: {}",
            email,
            savedWallet.getBalance()
    );

    return savedWallet;
}
    public String getCurrentUserEmail() {

        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }
    public List<WalletTransaction>
getWalletTransactions() {

    String email =
            getCurrentUserEmail();

    Wallet wallet =
            walletRepository
                    .findByUserEmail(
                            email
                    );

    if (wallet == null) {

        throw new ResourceNotFoundException(
                "Wallet not found"
        );
    }

    return walletTransactionRepository
            .findByWalletOrderByCreatedAtDesc(
                    wallet
            );
}
@Transactional
public String transferMoney(
        TransferMoneyRequest request
) {

    String senderEmail =
            getCurrentUserEmail();

    String receiverEmail =
            request.getReceiverEmail();

    if (senderEmail.equals(receiverEmail)) {

        throw new BadRequestException(
                "Cannot transfer money to yourself"
        );
    }

    Wallet senderWallet =
            walletRepository.findByUserEmail(
                    senderEmail
            );

    Wallet receiverWallet =
            walletRepository.findByUserEmail(
                    receiverEmail
            );

    if (receiverWallet == null) {

        throw new ResourceNotFoundException(
                "Receiver wallet not found"
        );
    }

    if (senderWallet.getBalance()
            .compareTo(request.getAmount()) < 0) {

        throw new BadRequestException(
                "Insufficient balance"
        );
    }

    senderWallet.setBalance(
            senderWallet.getBalance()
                    .subtract(request.getAmount())
    );

    receiverWallet.setBalance(
            receiverWallet.getBalance()
                    .add(request.getAmount())
    );

    walletRepository.save(
            senderWallet
    );

    walletRepository.save(
            receiverWallet
    );
    Transaction transaction =
        new Transaction(

                senderEmail,

                receiverEmail,

                request.getAmount(),

                TransactionStatus.SUCCESS,

                LocalDateTime.now()

        );

transactionRepository.save(
        transaction
);
notificationService
        .createNotification(

                senderEmail,

                "₹" +
                request.getAmount() +
                " transferred to " +
                receiverEmail

        );
notificationService
        .createNotification(

                receiverEmail,

                "₹" +
                request.getAmount() +
                " received from " +
                senderEmail

        );
WalletTransaction senderTx =
        new WalletTransaction();

senderTx.setAmount(
        request.getAmount()
);

senderTx.setType(
        "TRANSFER"
);

senderTx.setCreatedAt(
        LocalDateTime.now()
);

senderTx.setWallet(
        senderWallet
);

walletTransactionRepository.save(
        senderTx
);

    return "Transfer Successful";
}
}