
package com.smartwallet.service;

import com.smartwallet.dto.SpendingPatternResponse;
import com.smartwallet.model.TransactionStatus;
import com.smartwallet.model.TransactionType;
import com.smartwallet.repository.TransactionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SpendingPatternService {

    private static final BigDecimal HIGH_CONCENTRATION_THRESHOLD =
            new BigDecimal("50");

    private static final BigDecimal MODERATE_CONCENTRATION_THRESHOLD =
            new BigDecimal("30");

    private final TransactionRepository transactionRepository;

    public SpendingPatternService(
            TransactionRepository transactionRepository) {

        this.transactionRepository = transactionRepository;
    }

    /**
     * Existing all-time spending pattern method.
     * Preserved for backward compatibility.
     */
    @Transactional(readOnly = true)
    public SpendingPatternResponse getSpendingPattern(
            String email) {

        return getSpendingPattern(email, null, null);
    }

    /**
     * Generates spending pattern analytics for an optional date range.
     */
    @Transactional(readOnly = true)
    public SpendingPatternResponse getSpendingPattern(
            String email,
            LocalDate fromDate,
            LocalDate toDate) {

        validateDateRange(fromDate, toDate);

        LocalDateTime startDateTime =
                fromDate != null
                        ? fromDate.atStartOfDay()
                        : null;

        LocalDateTime endDateTimeExclusive =
                toDate != null
                        ? toDate.plusDays(1).atStartOfDay()
                        : null;

        List<Object[]> results =
                transactionRepository.getAnalyticsCategorySpending(
                        email,
                        TransactionStatus.SUCCESS,
                        List.of(
                                TransactionType.DEBIT,
                                TransactionType.TRANSFER
                        ),
                        startDateTime,
                        endDateTimeExclusive
                );

        if (results == null || results.isEmpty()) {
            return emptyResponse();
        }

        BigDecimal totalSpending = BigDecimal.ZERO;
        BigDecimal largestTransaction = BigDecimal.ZERO;

        long totalTransactions = 0;
        long highValueTransactionCount = 0;

        String topCategory = null;
        BigDecimal topCategoryAmount = BigDecimal.ZERO;

        for (Object[] row : results) {

            if (row == null || row.length < 5) {
                continue;
            }

            BigDecimal categoryAmount =
                    toBigDecimal(row[1]);

            long categoryTransactionCount =
                    toLong(row[2]);

            BigDecimal categoryLargestTransaction =
                    toBigDecimal(row[3]);

            long categoryHighValueCount =
                    toLong(row[4]);

            totalSpending =
                    totalSpending.add(categoryAmount);

            totalTransactions += categoryTransactionCount;

            highValueTransactionCount += categoryHighValueCount;

            if (categoryLargestTransaction.compareTo(
                    largestTransaction) > 0) {

                largestTransaction = categoryLargestTransaction;
            }

            // Results are ordered by category spending descending.
            if (topCategory == null) {

                topCategory =
                        row[0] != null
                                ? row[0].toString()
                                : "UNCATEGORIZED";

                topCategoryAmount = categoryAmount;
            }
        }

        if (totalTransactions == 0) {
            return emptyResponse();
        }

        BigDecimal averageSpending =
                totalSpending.divide(
                        BigDecimal.valueOf(totalTransactions),
                        2,
                        RoundingMode.HALF_UP
                );

        double topCategoryPercentage = 0.0;

        if (totalSpending.compareTo(BigDecimal.ZERO) > 0) {

            topCategoryPercentage =
                    topCategoryAmount
                            .multiply(new BigDecimal("100"))
                            .divide(
                                    totalSpending,
                                    2,
                                    RoundingMode.HALF_UP
                            )
                            .doubleValue();
        }

        int activeCategories = results.size();

        String spendingConcentration =
                determineSpendingConcentration(
                        topCategoryPercentage
                );

        return new SpendingPatternResponse(
                totalSpending,
                averageSpending,
                largestTransaction,
                topCategory,
                topCategoryAmount,
                topCategoryPercentage,
                activeCategories,
                highValueTransactionCount,
                spendingConcentration
        );
    }

    private void validateDateRange(
            LocalDate fromDate,
            LocalDate toDate) {

        if (fromDate != null
                && toDate != null
                && fromDate.isAfter(toDate)) {

            throw new IllegalArgumentException(
                    "From date cannot be after to date."
            );
        }
    }

    private String determineSpendingConcentration(
            double percentage) {

        BigDecimal concentration =
                BigDecimal.valueOf(percentage);

        if (concentration.compareTo(
                HIGH_CONCENTRATION_THRESHOLD) >= 0) {

            return "HIGH";
        }

        if (concentration.compareTo(
                MODERATE_CONCENTRATION_THRESHOLD) >= 0) {

            return "MODERATE";
        }

        return "DISTRIBUTED";
    }

    private SpendingPatternResponse emptyResponse() {

        return new SpendingPatternResponse(
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                null,
                BigDecimal.ZERO,
                0.0,
                0,
                0L,
                "DISTRIBUTED"
        );
    }

    private BigDecimal toBigDecimal(Object value) {

        if (value == null) {
            return BigDecimal.ZERO;
        }

        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }

        if (value instanceof Number) {
            return BigDecimal.valueOf(
                    ((Number) value).doubleValue()
            );
        }

        return new BigDecimal(value.toString());
    }

    private long toLong(Object value) {

        if (value == null) {
            return 0L;
        }

        return ((Number) value).longValue();
    }
}
