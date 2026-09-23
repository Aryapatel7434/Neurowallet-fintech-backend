package com.smartwallet.service;

import com.smartwallet.dto.SpendingPatternResponse;
import com.smartwallet.model.Transaction;
import com.smartwallet.model.TransactionCategory;
import com.smartwallet.model.TransactionStatus;
import com.smartwallet.model.TransactionType;
import com.smartwallet.repository.TransactionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class SpendingPatternService {

    private final TransactionRepository transactionRepository;

    public SpendingPatternService(
            TransactionRepository transactionRepository) {

        this.transactionRepository =
                transactionRepository;
    }

    public SpendingPatternResponse
            getSpendingPattern(String email) {

        List<Transaction> transactions =
                transactionRepository
                        .findBySenderEmailOrReceiverEmail(
                                email,
                                email
                        );

        BigDecimal totalSpending =
                BigDecimal.ZERO;

        BigDecimal largestTransaction =
                BigDecimal.ZERO;

        long spendingTransactionCount = 0;

        long highValueTransactionCount = 0;

        Map<TransactionCategory, BigDecimal>
                categoryTotals =
                new EnumMap<>(
                        TransactionCategory.class
                );

        for (Transaction transaction : transactions) {

            if (transaction == null
                    || transaction.getAmount() == null
                    || transaction.getStatus() == null
                    || transaction.getType() == null
                    || transaction.getCategory() == null) {

                continue;
            }

            if (transaction.getStatus()
                    != TransactionStatus.SUCCESS) {

                continue;
            }

            /*
             * Spending means money sent by the user.
             */
            if (!email.equals(
                    transaction.getSenderEmail())) {

                continue;
            }

            if (transaction.getType()
                    != TransactionType.DEBIT
                    && transaction.getType()
                    != TransactionType.TRANSFER) {

                continue;
            }

            BigDecimal amount =
                    transaction.getAmount().abs();

            totalSpending =
                    totalSpending.add(amount);

            spendingTransactionCount++;

            if (amount.compareTo(
                    largestTransaction) > 0) {

                largestTransaction = amount;
            }

            /*
             * High-value transaction threshold.
             *
             * Current project threshold:
             * ₹10,000
             */
            if (amount.compareTo(
                    new BigDecimal("10000")) >= 0) {

                highValueTransactionCount++;
            }

            TransactionCategory category =
                    transaction.getCategory();

            categoryTotals.put(
                    category,
                    categoryTotals.getOrDefault(
                            category,
                            BigDecimal.ZERO
                    ).add(amount)
            );
        }

        BigDecimal averageSpending =
                BigDecimal.ZERO;

        if (spendingTransactionCount > 0) {

            averageSpending =
                    totalSpending
                            .divide(
                                    BigDecimal.valueOf(
                                            spendingTransactionCount
                                    ),
                                    2,
                                    RoundingMode.HALF_UP
                            );
        }

        TransactionCategory topCategory =
                null;

        BigDecimal topCategoryAmount =
                BigDecimal.ZERO;

        for (Map.Entry<TransactionCategory, BigDecimal> entry
                : categoryTotals.entrySet()) {

            if (entry.getValue()
                    .compareTo(topCategoryAmount) > 0) {

                topCategory =
                        entry.getKey();

                topCategoryAmount =
                        entry.getValue();
            }
        }

        double topCategoryPercentage = 0.0;

        if (totalSpending.compareTo(
                BigDecimal.ZERO) > 0) {

            topCategoryPercentage =
                    topCategoryAmount
                            .multiply(
                                    new BigDecimal("100")
                            )
                            .divide(
                                    totalSpending,
                                    2,
                                    RoundingMode.HALF_UP
                            )
                            .doubleValue();
        }

        int activeCategories =
                categoryTotals.size();

        String spendingConcentration =
                determineSpendingConcentration(
                        topCategoryPercentage
                );

        return new SpendingPatternResponse(
                totalSpending,
                averageSpending,
                largestTransaction,
                topCategory != null
                        ? topCategory.name()
                        : null,
                topCategoryAmount,
                topCategoryPercentage,
                activeCategories,
                highValueTransactionCount,
                spendingConcentration
        );
    }

    private String determineSpendingConcentration(
            double topCategoryPercentage) {

        if (topCategoryPercentage >= 50) {
            return "HIGH";
        }

        if (topCategoryPercentage >= 30) {
            return "MODERATE";
        }

        return "DISTRIBUTED";
    }
}