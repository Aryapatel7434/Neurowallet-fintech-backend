
package com.smartwallet.service;

import com.smartwallet.dto.AccountDataExportResponse;
import com.smartwallet.model.Transaction;
import com.smartwallet.model.User;
import com.smartwallet.model.UserNotificationPreferences;
import com.smartwallet.model.UserPreferences;
import com.smartwallet.model.Wallet;
import com.smartwallet.repository.TransactionRepository;
import com.smartwallet.repository.UserNotificationPreferencesRepository;
import com.smartwallet.repository.UserPreferencesRepository;
import com.smartwallet.repository.UserRepository;
import com.smartwallet.repository.WalletRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountDataExportService {

    private static final int MAX_EXPORT_TRANSACTIONS = 5000;

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final UserPreferencesRepository preferencesRepository;
    private final UserNotificationPreferencesRepository
            notificationPreferencesRepository;

    public AccountDataExportService(
            UserRepository userRepository,
            WalletRepository walletRepository,
            TransactionRepository transactionRepository,
            UserPreferencesRepository preferencesRepository,
            UserNotificationPreferencesRepository
                    notificationPreferencesRepository) {

        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.preferencesRepository = preferencesRepository;
        this.notificationPreferencesRepository =
                notificationPreferencesRepository;
    }

    @Transactional(readOnly = true)
    public AccountDataExportResponse exportMyAccountData(String email) {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        Wallet wallet = walletRepository.findByUserUserId(
                user.getUserId()
        );

        if (wallet == null) {
            throw new IllegalStateException(
                    "Wallet information is unavailable"
            );
        }

        /*
         * Fetch at most 5,001 records.
         *
         * The extra record determines whether additional transactions
         * exist beyond the maximum export size.
         */
         Pageable exportPage = PageRequest.of(
        0,
        MAX_EXPORT_TRANSACTIONS + 1,
        Sort.by(
                Sort.Order.desc("timestamp"),
                Sort.Order.desc("transactionId")
        )
);
        Page<Transaction> transactionPage =
                transactionRepository
                        .findBySenderEmailOrReceiverEmail(
                                email,
                                email,
                                exportPage
                        );

        List<Transaction> fetchedTransactions =
                transactionPage.getContent();

        boolean transactionsComplete =
                fetchedTransactions.size()
                        <= MAX_EXPORT_TRANSACTIONS;

        List<AccountDataExportResponse.ExportTransaction> transactions =
                fetchedTransactions.stream()
                        .limit(MAX_EXPORT_TRANSACTIONS)
                        .map(this::toExportTransaction)
                        .toList();

        UserPreferences preferences =
                preferencesRepository.findByUser_UserId(user.getUserId())
                        .orElse(null);

        UserNotificationPreferences notificationPreferences =
                notificationPreferencesRepository
                        .findByUser_UserId(user.getUserId())
                        .orElse(null);

        AccountDataExportResponse.ExportPreferences exportedPreferences =
                preferences == null
                        ? null
                        : new AccountDataExportResponse.ExportPreferences(
                                preferences.getTheme(),
                                preferences.getPreferredCurrency(),
                                preferences.getLanguage()
                        );

        AccountDataExportResponse.ExportNotificationPreferences
                exportedNotificationPreferences =
                notificationPreferences == null
                        ? null
                        : new AccountDataExportResponse
                                .ExportNotificationPreferences(
                                        notificationPreferences
                                                .isEmailNotifications(),
                                        notificationPreferences
                                                .isTransactionAlerts(),
                                        notificationPreferences
                                                .isSecurityAlerts()
                                );

        AccountDataExportResponse.ExportProfile profile =
                new AccountDataExportResponse.ExportProfile(
                        user.getUserId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole()
                );

        AccountDataExportResponse.ExportWallet exportedWallet =
                new AccountDataExportResponse.ExportWallet(
                        wallet.getWalletId(),
                        wallet.getBalance(),
                        wallet.getCurrency(),
                        wallet.getStatus(),
                        wallet.getCreatedAt(),
                        wallet.getUpdatedAt()
                );

        return new AccountDataExportResponse(
                profile,
                exportedWallet,
                transactions,
                exportedPreferences,
                exportedNotificationPreferences,
                LocalDateTime.now(),
                transactionsComplete,
                transactions.size(),
                MAX_EXPORT_TRANSACTIONS
        );
    }

    private AccountDataExportResponse.ExportTransaction
            toExportTransaction(Transaction transaction) {

        return new AccountDataExportResponse.ExportTransaction(
                transaction.getTransactionId(),
                transaction.getSenderEmail(),
                transaction.getReceiverEmail(),
                transaction.getAmount(),
                transaction.getStatus().name(),
                transaction.getType().name(),
                transaction.getCategory().name(),
                transaction.getTimestamp()
        );
    }
}
