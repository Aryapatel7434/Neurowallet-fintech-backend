
package com.smartwallet.service;

import com.smartwallet.dto.CategorySpendingResponse;
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
public class CategoryAnalysisService {

    private final TransactionRepository transactionRepository;

    public CategoryAnalysisService(
            TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional(readOnly = true)
    public List<CategorySpendingResponse> getCategorySpending(
            String email) {
        return getCategorySpending(email, null, null);
    }

    @Transactional(readOnly = true)
    public List<CategorySpendingResponse> getCategorySpending(
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
            return List.of();
        }

        BigDecimal totalSpending = BigDecimal.ZERO;

        for (Object[] row : results) {
            if (row != null
                    && row.length > 1
                    && row[1] != null) {

                totalSpending = totalSpending.add(
                        toBigDecimal(row[1])
                );
            }
        }

        // FIX: Use an effectively final variable inside the lambda.
        final BigDecimal calculatedTotalSpending = totalSpending;

        return results.stream()
                .filter(row -> row != null && row.length > 1)
                .map(row -> {

                    String category =
                            row[0] != null
                                    ? row[0].toString()
                                    : "UNCATEGORIZED";

                    BigDecimal amount =
                            toBigDecimal(row[1]);

                    double percentage = 0.0;

                    if (calculatedTotalSpending.compareTo(
                            BigDecimal.ZERO) > 0) {

                        percentage = amount
                                .multiply(new BigDecimal("100"))
                                .divide(
                                        calculatedTotalSpending,
                                        2,
                                        RoundingMode.HALF_UP
                                )
                                .doubleValue();
                    }

                    return new CategorySpendingResponse(
                            category,
                            amount,
                            percentage
                    );
                })
                .toList();
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
}
