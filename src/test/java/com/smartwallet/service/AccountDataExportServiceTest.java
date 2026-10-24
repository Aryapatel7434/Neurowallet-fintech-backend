package com.smartwallet.service;

import com.smartwallet.dto.AccountDataExportResponse;
import com.smartwallet.model.Transaction;
import com.smartwallet.model.TransactionCategory;
import com.smartwallet.model.TransactionStatus;
import com.smartwallet.model.TransactionType;
import com.smartwallet.model.User;
import com.smartwallet.model.Wallet;
import com.smartwallet.repository.TransactionRepository;
import com.smartwallet.repository.UserNotificationPreferencesRepository;
import com.smartwallet.repository.UserPreferencesRepository;
import com.smartwallet.repository.UserRepository;
import com.smartwallet.repository.WalletRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountDataExportServiceTest {

    private static final String EMAIL = "arya@gmail.com";
    private static final int MAX_TRANSACTIONS = 5000;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserPreferencesRepository preferencesRepository;

    @Mock
    private UserNotificationPreferencesRepository
            notificationPreferencesRepository;

    private AccountDataExportService exportService;

    @BeforeEach
    void setUp() {
        exportService = new AccountDataExportService(
                userRepository,
                walletRepository,
                transactionRepository,
                preferencesRepository,
                notificationPreferencesRepository
        );
    }

    // ---------------------------------------------------------
    // Test data helpers
    // ---------------------------------------------------------

    private User createUser() {
        User user = mock(User.class);

        when(user.getUserId()).thenReturn(1);
        when(user.getName()).thenReturn("Arya Patel");
        when(user.getEmail()).thenReturn(EMAIL);
        when(user.getRole()).thenReturn("USER");

        return user;
    }

    private Wallet createWallet() {
        Wallet wallet = mock(Wallet.class);

        when(wallet.getWalletId()).thenReturn(10L);
        when(wallet.getBalance())
                .thenReturn(new BigDecimal("1000.00"));
        when(wallet.getCurrency()).thenReturn("INR");
        when(wallet.getStatus()).thenReturn("ACTIVE");

        return wallet;
    }

    private void mockValidAccount() {
        // Complete the helper stubbing before repository stubbing.
        // This avoids nested Mockito stubbing inside thenReturn(...).
        User user = createUser();
        Wallet wallet = createWallet();

        when(userRepository.findByEmail(EMAIL))
                .thenReturn(user);

        when(walletRepository.findByUserUserId(1))
                .thenReturn(wallet);

        when(preferencesRepository.findByUser_UserId(1))
                .thenReturn(Optional.empty());

        when(notificationPreferencesRepository.findByUser_UserId(1))
                .thenReturn(Optional.empty());
    }

    private void mockTransactions(List<Transaction> transactions) {
        when(transactionRepository.findBySenderEmailOrReceiverEmail(
                eq(EMAIL),
                eq(EMAIL),
                any(Pageable.class)
        )).thenAnswer(invocation -> {
            Pageable pageable = invocation.getArgument(2);

            int start = (int) pageable.getOffset();
            int end = Math.min(
                    start + pageable.getPageSize(),
                    transactions.size()
            );

            List<Transaction> content =
                    start >= transactions.size()
                            ? List.of()
                            : transactions.subList(start, end);

            return new PageImpl<>(
                    content,
                    pageable,
                    transactions.size()
            );
        });
    }

    private List<Transaction> createTransactions(int count) {
        LocalDateTime baseTime =
                LocalDateTime.of(2026, 1, 1, 12, 0);

        return IntStream.range(0, count)
                .mapToObj(index -> {
                    Transaction transaction = mock(Transaction.class);

                    /*
                     * For the 5001st record, the service only needs
                     * to know that an extra record exists.
                     *
                     * It exports at most 5000 records, so this extra
                     * mock must not have unused field stubbings.
                     */
                    if (index >= MAX_TRANSACTIONS) {
                        return transaction;
                    }

                    when(transaction.getTransactionId())
                            .thenReturn((long) index + 1);

                    when(transaction.getSenderEmail())
                            .thenReturn(EMAIL);

                    when(transaction.getReceiverEmail())
                            .thenReturn("receiver@example.com");

                    when(transaction.getAmount())
                            .thenReturn(new BigDecimal("10.00"));

                    when(transaction.getStatus())
                            .thenReturn(TransactionStatus.SUCCESS);

                    when(transaction.getType())
                            .thenReturn(TransactionType.TRANSFER);

                    when(transaction.getCategory())
                            .thenReturn(TransactionCategory.OTHER);

                    when(transaction.getTimestamp())
                            .thenReturn(baseTime.plusMinutes(index));

                    return transaction;
                })
                .toList();
    }

    // ---------------------------------------------------------
    // Successful export tests
    // ---------------------------------------------------------

    @Test
    void shouldExportEmptyTransactionHistoryAsComplete() {
        mockValidAccount();
        mockTransactions(List.of());

        AccountDataExportResponse response =
                exportService.exportMyAccountData(EMAIL);

        assertNotNull(response);
        assertTrue(response.getTransactions().isEmpty());
        assertTrue(response.isTransactionsComplete());
        assertEquals(0, response.getExportedTransactionCount());
        assertEquals(
                MAX_TRANSACTIONS,
                response.getMaxTransactions()
        );
    }

    @Test
    void shouldExportAll250Transactions() {
        mockValidAccount();
        mockTransactions(createTransactions(250));

        AccountDataExportResponse response =
                exportService.exportMyAccountData(EMAIL);

        assertNotNull(response);
        assertEquals(250, response.getTransactions().size());
        assertTrue(response.isTransactionsComplete());
        assertEquals(250, response.getExportedTransactionCount());
        assertEquals(
                MAX_TRANSACTIONS,
                response.getMaxTransactions()
        );
    }

    @Test
    void shouldExportExactly5000TransactionsAsComplete() {
        mockValidAccount();
        mockTransactions(createTransactions(MAX_TRANSACTIONS));

        AccountDataExportResponse response =
                exportService.exportMyAccountData(EMAIL);

        assertEquals(
                MAX_TRANSACTIONS,
                response.getTransactions().size()
        );
        assertTrue(response.isTransactionsComplete());
        assertEquals(
                MAX_TRANSACTIONS,
                response.getExportedTransactionCount()
        );
        assertEquals(
                MAX_TRANSACTIONS,
                response.getMaxTransactions()
        );
    }

    @Test
    void shouldTruncateWhenMoreThan5000TransactionsExist() {
        mockValidAccount();
        mockTransactions(createTransactions(MAX_TRANSACTIONS + 1));

        AccountDataExportResponse response =
                exportService.exportMyAccountData(EMAIL);

        assertEquals(
                MAX_TRANSACTIONS,
                response.getTransactions().size()
        );
        assertFalse(response.isTransactionsComplete());
        assertEquals(
                MAX_TRANSACTIONS,
                response.getExportedTransactionCount()
        );
        assertEquals(
                MAX_TRANSACTIONS,
                response.getMaxTransactions()
        );
    }

    // ---------------------------------------------------------
    // Ordering test
    // ---------------------------------------------------------

    @Test
    void shouldRequestNewestFirstOrderingWithTransactionIdTieBreaker() {
        mockValidAccount();
        mockTransactions(List.of());

        exportService.exportMyAccountData(EMAIL);

        ArgumentCaptor<Pageable> captor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(transactionRepository)
                .findBySenderEmailOrReceiverEmail(
                        eq(EMAIL),
                        eq(EMAIL),
                        captor.capture()
                );

        Pageable pageable = captor.getValue();

        assertEquals(0, pageable.getPageNumber());
        assertEquals(
                MAX_TRANSACTIONS + 1,
                pageable.getPageSize()
        );

        assertEquals(
                List.of("timestamp: DESC", "transactionId: DESC"),
                pageable.getSort()
                        .stream()
                        .map(order -> order.getProperty()
                                + ": " + order.getDirection())
                        .toList()
        );
    }

    // ---------------------------------------------------------
    // Failure tests
    // ---------------------------------------------------------

    @Test
    void shouldFailWhenUserDoesNotExist() {
        when(userRepository.findByEmail(EMAIL))
                .thenReturn(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> exportService.exportMyAccountData(EMAIL)
        );

        verifyNoInteractions(
                walletRepository,
                transactionRepository,
                preferencesRepository,
                notificationPreferencesRepository
        );
    }

  @Test
void shouldFailWhenWalletDoesNotExist() {
    // Stub only the user ID required by the service.
    User user = mock(User.class);
    when(user.getUserId()).thenReturn(1);

    when(userRepository.findByEmail(EMAIL))
            .thenReturn(user);

    when(walletRepository.findByUserUserId(1))
            .thenReturn(null);

    assertThrows(
            IllegalStateException.class,
            () -> exportService.exportMyAccountData(EMAIL)
    );

    verifyNoInteractions(
            transactionRepository,
            preferencesRepository,
            notificationPreferencesRepository
    );
}
}
