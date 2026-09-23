package com.smartwallet.service;

import com.smartwallet.dto.CategorySpendingResponse;
import com.smartwallet.model.Transaction;
import com.smartwallet.model.TransactionStatus;
import com.smartwallet.model.TransactionCategory;
import com.smartwallet.model.TransactionType;
import com.smartwallet.repository.TransactionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class CategoryAnalysisService {

    private final TransactionRepository transactionRepository;

    public CategoryAnalysisService(
            TransactionRepository transactionRepository) {

        this.transactionRepository =
                transactionRepository;
    }

    public List<CategorySpendingResponse>
            getCategorySpending(String email) {

        List<Transaction> transactions =
                transactionRepository
                        .findBySenderEmailOrReceiverEmail(
                                email,
                                email
                        );

        Map<TransactionCategory, BigDecimal>
                categoryTotals =
                new EnumMap<>(
                        TransactionCategory.class
                );

        BigDecimal totalExpense =
                BigDecimal.ZERO;

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
             * Only money sent by the authenticated user
             * is treated as spending.
             */
            if (transaction.getType()
                    != TransactionType.DEBIT
                    && transaction.getType()
                    != TransactionType.TRANSFER) {

                continue;
            }

            if (!email.equals(
                    transaction.getSenderEmail())) {

                continue;
            }

            BigDecimal amount =
                    transaction.getAmount().abs();

            TransactionCategory category =
                    transaction.getCategory();

            categoryTotals.put(
                    category,
                    categoryTotals.getOrDefault(
                            category,
                            BigDecimal.ZERO
                    ).add(amount)
            );

            totalExpense =
                    totalExpense.add(amount);
        }

        List<CategorySpendingResponse> response =
                new ArrayList<>();

        for (Map.Entry<TransactionCategory, BigDecimal> entry
                : categoryTotals.entrySet()) {

            BigDecimal amount =
                    entry.getValue();

            double percentage = 0.0;

            if (totalExpense.compareTo(
                    BigDecimal.ZERO) > 0) {

                percentage =
                        amount
                                .multiply(
                                        new BigDecimal("100")
                                )
                                .divide(
                                        totalExpense,
                                        2,
                                        RoundingMode.HALF_UP
                                )
                                .doubleValue();
            }

            response.add(
                    new CategorySpendingResponse(
                            entry.getKey().name(),
                            amount,
                            percentage
                    )
            );
        }

        /*
         * Highest spending category first.
         */
        response.sort(
                (a, b) ->
                        b.getAmount()
                                .compareTo(
                                        a.getAmount()
                                )
        );

        return response;
    }
}