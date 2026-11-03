
package com.smartwallet.service;

import com.smartwallet.model.TransactionCategory;
import com.smartwallet.model.TransactionStatus;
import com.smartwallet.model.TransactionType;
import com.smartwallet.repository.TransactionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class BudgetSpendingService {

    private final TransactionRepository transactionRepository;

    public BudgetSpendingService(
            TransactionRepository transactionRepository
    ) {
        this.transactionRepository = transactionRepository;
    }

    // =========================================================
    // CATEGORY-WISE SPENDING
    // =========================================================

    public Map<TransactionCategory, BigDecimal> getCategorySpending(
            String email,
            LocalDate periodStart,
            LocalDate periodEnd
    ) {

        validateInput(email, periodStart, periodEnd);

        Map<TransactionCategory, BigDecimal> spending =
                new EnumMap<>(TransactionCategory.class);

        // Initialize all categories to zero.
        for (TransactionCategory category :
                TransactionCategory.values()) {

            spending.put(category, BigDecimal.ZERO);
        }

        List<TransactionType> outgoingTypes = List.of(
                TransactionType.DEBIT,
                TransactionType.TRANSFER
        );

        List<Object[]> results =
                transactionRepository.getBudgetCategorySpending(
                        email.trim(),
                        TransactionStatus.SUCCESS,
                        outgoingTypes,
                        periodStart.atStartOfDay(),
                        periodEnd.plusDays(1).atStartOfDay()
                );

        // Populate actual database spending.
        for (Object[] result : results) {

            if (result == null || result.length < 2) {
                continue;
            }

            TransactionCategory category =
                    (TransactionCategory) result[0];

            BigDecimal amount = convertAmount(result[1]);

            if (category != null) {

                spending.merge(
                        category,
                        amount,
                        BigDecimal::add
                );
            }
        }

        return spending;
    }

    // =========================================================
    // SPENDING FOR ONE CATEGORY
    // =========================================================

    public BigDecimal getCategoryAmount(
            String email,
            TransactionCategory category,
            LocalDate periodStart,
            LocalDate periodEnd
    ) {

        if (category == null) {
            throw new IllegalArgumentException(
                    "Transaction category is required."
            );
        }

        return getCategorySpending(
                email,
                periodStart,
                periodEnd
        ).getOrDefault(category, BigDecimal.ZERO);
    }

    // =========================================================
    // TOTAL SPENDING
    // =========================================================

    public BigDecimal getTotalSpending(
            String email,
            LocalDate periodStart,
            LocalDate periodEnd
    ) {

        return getCategorySpending(
                email,
                periodStart,
                periodEnd
        ).values()
         .stream()
         .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // =========================================================
    // VALIDATION
    // =========================================================

    private void validateInput(
            String email,
            LocalDate periodStart,
            LocalDate periodEnd
    ) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "User email is required."
            );
        }

        if (periodStart == null || periodEnd == null) {
            throw new IllegalArgumentException(
                    "Budget period dates are required."
            );
        }

        if (periodEnd.isBefore(periodStart)) {
            throw new IllegalArgumentException(
                    "Budget end date cannot be before start date."
            );
        }
    }

    // =========================================================
    // SAFE AMOUNT CONVERSION
    // =========================================================

    private BigDecimal convertAmount(Object value) {

        if (value == null) {
            return BigDecimal.ZERO;
        }

        if (value instanceof BigDecimal amount) {
            return amount;
        }

        if (value instanceof Number number) {
            return new BigDecimal(number.toString());
        }

        throw new IllegalStateException(
                "Unexpected transaction amount type: "
                        + value.getClass().getName()
        );
    }
}
