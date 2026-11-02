
package com.smartwallet.service;

import com.smartwallet.dto.ScheduleTransactionRequest;
import com.smartwallet.dto.ScheduledTransactionResponse;
import com.smartwallet.model.ScheduledTransaction;
import com.smartwallet.model.TransactionStatus;
import com.smartwallet.repository.ScheduledTransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScheduledTransactionService {

    private final ScheduledTransactionRepository repository;

    public ScheduledTransactionService(
            ScheduledTransactionRepository repository) {
        this.repository = repository;
    }

    // ============================================================
    // CREATE SCHEDULED TRANSACTION
    // ============================================================

    @Transactional
    public String scheduleTransaction(
            ScheduleTransactionRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Schedule request cannot be empty");
        }

        String senderEmail = getAuthenticatedUserEmail();

        if (request.getReceiverEmail() == null
                || request.getReceiverEmail().isBlank()) {
            throw new IllegalArgumentException(
                    "Receiver email is required");
        }

        String receiverEmail = request.getReceiverEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (!receiverEmail.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException(
                    "Invalid receiver email");
        }

        if (senderEmail.equals(receiverEmail)) {
            throw new IllegalArgumentException(
                    "You cannot schedule a payment to yourself");
        }

        BigDecimal amount = request.getAmount();

        if (amount == null
                || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Transaction amount must be greater than zero");
        }

        if (amount.scale() > 2 || amount.precision() > 19) {
            throw new IllegalArgumentException(
                    "Invalid transaction amount precision");
        }

        LocalDateTime scheduledTime = request.getScheduledTime();

        if (scheduledTime == null
                || !scheduledTime.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Scheduled time must be in the future");
        }

        ScheduledTransaction transaction =
                new ScheduledTransaction(
                        senderEmail,
                        receiverEmail,
                        amount,
                        scheduledTime,
                        TransactionStatus.PENDING
                );

        repository.save(transaction);

        return "Transaction Scheduled Successfully";
    }

    // ============================================================
    // GET AUTHENTICATED USER'S SCHEDULED TRANSACTIONS
    // ============================================================

    @Transactional(readOnly = true)
    public Page<ScheduledTransactionResponse> getMyScheduledTransactions(
            int page,
            int size) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page number cannot be negative");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100");
        }

        String senderEmail = getAuthenticatedUserEmail();

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "scheduledTime")
        );

        return repository.findBySenderEmail(senderEmail, pageable)
                .map(this::toResponse);
    }

    // ============================================================
    // GET SCHEDULED TRANSACTION BY ID
    // ============================================================

    @Transactional(readOnly = true)
    public ScheduledTransactionResponse getScheduledTransactionById(
            Long scheduledTransactionId) {

        validateTransactionId(scheduledTransactionId);

        String senderEmail = getAuthenticatedUserEmail();

        ScheduledTransaction transaction = repository
                .findByScheduledTransactionIdAndSenderEmail(
                        scheduledTransactionId,
                        senderEmail
                )
                .orElseThrow(() -> new NoSuchElementException(
                        "Scheduled transaction not found"));

        return toResponse(transaction);
    }

    // ============================================================
    // CANCEL SCHEDULED TRANSACTION
    // ============================================================

    @Transactional
    public String cancelScheduledTransaction(
            Long scheduledTransactionId) {

        validateTransactionId(scheduledTransactionId);

        String senderEmail = getAuthenticatedUserEmail();

        // Acquire the same database lock used by execution.
        ScheduledTransaction transaction = repository
                .findByIdForUpdate(scheduledTransactionId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Scheduled transaction not found"));

        // Verify ownership after acquiring the lock.
        if (!senderEmail.equalsIgnoreCase(
                transaction.getSenderEmail())) {

            throw new NoSuchElementException(
                    "Scheduled transaction not found");
        }

        // Only pending transactions can be cancelled.
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending scheduled transactions can be cancelled");
        }

        transaction.setStatus(TransactionStatus.CANCELLED);
        transaction.setFailureReason(null);

        repository.save(transaction);

        return "Scheduled transaction cancelled successfully";
    }

    // ============================================================
    // ENTITY TO RESPONSE DTO
    // ============================================================

    private ScheduledTransactionResponse toResponse(
            ScheduledTransaction transaction) {

        return new ScheduledTransactionResponse(
                transaction.getScheduledTransactionId(),
                transaction.getReceiverEmail(),
                transaction.getAmount(),
                transaction.getScheduledTime(),
                transaction.getStatus(),
                transaction.getExecutedAt(),
                transaction.getFailureReason(),
                transaction.getCreatedAt()
        );
    }

    // ============================================================
    // AUTHENTICATION
    // ============================================================

    private String getAuthenticatedUserEmail() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || authentication.getName().isBlank()
                || "anonymousUser".equals(authentication.getName())) {

            throw new IllegalStateException(
                    "User authentication is required");
        }

        return authentication.getName()
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    // ============================================================
    // ID VALIDATION
    // ============================================================

    private void validateTransactionId(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "Invalid scheduled transaction ID");
        }
    }
}
