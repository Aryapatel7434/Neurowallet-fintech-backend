
package com.smartwallet.service;

import com.smartwallet.dto.TransactionAnalyticsResponse;
import com.smartwallet.model.TransactionStatus;
import com.smartwallet.model.TransactionType;
import com.smartwallet.repository.TransactionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TransactionAnalyticsService {

    private static final Logger logger =
            LoggerFactory.getLogger(TransactionAnalyticsService.class);

    private final TransactionRepository transactionRepository;

    public TransactionAnalyticsService(
            TransactionRepository transactionRepository) {

        this.transactionRepository = transactionRepository;
    }

    /**
     * Returns all-time transaction analytics.
     */
    public TransactionAnalyticsResponse getTransactionAnalytics(
            String email) {

        return getTransactionAnalytics(email, null, null);
    }

    /**
     * Returns transaction analytics for an optional date range.
     */
    public TransactionAnalyticsResponse getTransactionAnalytics(
            String email,
            LocalDate fromDate,
            LocalDate toDate) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "User email must not be empty."
            );
        }

        if (fromDate != null
                && toDate != null
                && fromDate.isAfter(toDate)) {

            throw new IllegalArgumentException(
                    "From date must not be after to date."
            );
        }

        LocalDateTime startDateTime =
                fromDate != null
                        ? fromDate.atStartOfDay()
                        : null;

        LocalDateTime endDateTimeExclusive =
                toDate != null
                        ? toDate.plusDays(1).atStartOfDay()
                        : null;

        logger.info(
                "Calculating transaction analytics. Date range: {} to {}",
                fromDate,
                toDate
        );

        Object[] rawResult =
                transactionRepository.getTransactionAnalyticsAggregation(
                        email,
                        TransactionStatus.SUCCESS,
                        TransactionStatus.FAILED,
                        TransactionType.CREDIT,
                        TransactionType.DEBIT,
                        TransactionType.TRANSFER,
                        startDateTime,
                        endDateTimeExclusive
                );

        Object[] result = normalizeResult(rawResult);

        long totalTransactions =
                getLongValue(result, 0);

        long successfulTransactions =
                getLongValue(result, 1);

        long failedTransactions =
                getLongValue(result, 2);

        BigDecimal totalIncome =
                getDecimalValue(result, 3);

        BigDecimal totalExpense =
                getDecimalValue(result, 4);

        BigDecimal totalTransfer =
                getDecimalValue(result, 5);

        BigDecimal averageTransaction =
                getDecimalValue(result, 6);

        BigDecimal largestTransaction =
                getDecimalValue(result, 7);

        BigDecimal savings =
                totalIncome.subtract(totalExpense);

        BigDecimal savingsRatio = BigDecimal.ZERO;

        if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {

            savingsRatio = savings
                    .multiply(BigDecimal.valueOf(100))
                    .divide(
                            totalIncome,
                            2,
                            RoundingMode.HALF_UP
                    );
        }

        logger.info(
                "Transaction analytics completed. Total: {}, Successful: {}, Failed: {}",
                totalTransactions,
                successfulTransactions,
                failedTransactions
        );

        return new TransactionAnalyticsResponse(
                totalTransactions,
                successfulTransactions,
                failedTransactions,
                totalIncome,
                totalExpense,
                totalTransfer,
                averageTransaction,
                largestTransaction,
                savings,
                savingsRatio
        );
    }

    /**
     * Handles both a normal aggregate result and a single-column
     * wrapper containing the aggregate Object[].
     */
    private Object[] normalizeResult(Object[] result) {

        if (result == null) {
            return new Object[0];
        }

        if (result.length == 1
                && result[0] instanceof Object[]) {

            return (Object[]) result[0];
        }

        return result;
    }

    private long getLongValue(
            Object[] result,
            int index) {

        if (result == null
                || index < 0
                || index >= result.length
                || result[index] == null) {

            return 0L;
        }

        Object value = result[index];

        if (value instanceof Number) {
            return ((Number) value).longValue();
        }

        return Long.parseLong(value.toString());
    }

    private BigDecimal getDecimalValue(
            Object[] result,
            int index) {

        if (result == null
                || index < 0
                || index >= result.length
                || result[index] == null) {

            return BigDecimal.ZERO;
        }

        Object value = result[index];

        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }

        if (value instanceof Number) {
            return new BigDecimal(value.toString());
        }

        return new BigDecimal(value.toString());
    }
}
