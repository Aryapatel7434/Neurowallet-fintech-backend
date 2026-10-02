package com.smartwallet.service;

import com.smartwallet.dto.BudgetAdvisorResponse;
import com.smartwallet.dto.FinancialHealthScoreResponse;
import com.smartwallet.dto.FinancialIntelligenceContext;
import com.smartwallet.dto.SpendingPatternResponse;
import com.smartwallet.dto.TransactionAnalyticsResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

@Service
public class FinancialHealthScoreService {

    private static final int MAX_SCORE = 100;

    private static final int SAVINGS_MAX = 25;
    private static final int BUDGET_MAX = 25;
    private static final int SPENDING_MAX = 20;
    private static final int CONCENTRATION_MAX = 15;
    private static final int RISK_MAX = 15;

    public FinancialHealthScoreResponse calculateScore(
            FinancialIntelligenceContext context) {

        validateContext(context);

        TransactionAnalyticsResponse analytics =
                context.getAnalytics();

        BudgetAdvisorResponse budgetAdvisor =
                context.getBudgetAdvisor();

        SpendingPatternResponse spendingPattern =
                context.getSpendingPattern();

        int savingsScore =
                calculateSavingsScore(analytics);

        int budgetScore =
                calculateBudgetScore(budgetAdvisor);

        int spendingScore =
                calculateSpendingScore(
                        analytics,
                        spendingPattern
                );

        int concentrationScore =
                calculateConcentrationScore(
                        spendingPattern
                );

        int riskScore =
                calculateRiskScore(
                        analytics
                );

        int totalScore =
                savingsScore
                + budgetScore
                + spendingScore
                + concentrationScore
                + riskScore;

        totalScore =
                clamp(
                        totalScore,
                        0,
                        MAX_SCORE
                );

        String level =
                determineHealthLevel(totalScore);

        String summary =
                buildSummary(
                        totalScore,
                        level
                );

        return new FinancialHealthScoreResponse(
                totalScore,
                level,
                summary,
                savingsScore,
                budgetScore,
                spendingScore,
                concentrationScore,
                riskScore
        );
    }

    /*
     * ============================================================
     * Savings component
     * ============================================================
     *
     * Maximum: 25 points
     *
     * Savings ratio:
     *
     * >= 20%  -> 25
     * >= 15%  -> 20
     * >= 10%  -> 15
     * >= 5%   -> 10
     * >  0%   -> 5
     * <= 0%   -> 0
     */
    private int calculateSavingsScore(
            TransactionAnalyticsResponse analytics) {

        BigDecimal savingsRatio =
                safeValue(
                        analytics.getSavingsRatio()
                );

        BigDecimal percentage =
                savingsRatio;

        /*
         * The existing analytics response stores
         * savingsRatio as a percentage value.
         *
         * Example:
         * 20.00 means 20%.
         */

        if (percentage.compareTo(
                new BigDecimal("20")) >= 0) {

            return SAVINGS_MAX;
        }

        if (percentage.compareTo(
                new BigDecimal("15")) >= 0) {

            return 20;
        }

        if (percentage.compareTo(
                new BigDecimal("10")) >= 0) {

            return 15;
        }

        if (percentage.compareTo(
                new BigDecimal("5")) >= 0) {

            return 10;
        }

        /*
         * Exactly 0% savings should receive 0 points.
         * A positive savings ratio below 5% receives 5 points.
         */
        if (percentage.compareTo(
                BigDecimal.ZERO) > 0) {

            return 5;
        }

        return 0;
    }

    /*
     * ============================================================
     * Budget component
     * ============================================================
     *
     * Maximum: 25 points
     *
     * Compares actual monthly expense
     * against monthly income.
     */
    private int calculateBudgetScore(
            BudgetAdvisorResponse budgetAdvisor) {

        BigDecimal income =
                safeValue(
                        budgetAdvisor.getMonthlyIncome()
                );

        BigDecimal expense =
                safeValue(
                        budgetAdvisor.getMonthlyExpense()
                );

        if (income.compareTo(
                BigDecimal.ZERO) <= 0) {

            return 0;
        }

        BigDecimal expenseRatio =
                expense
                        .divide(
                                income,
                                4,
                                RoundingMode.HALF_UP
                        )
                        .multiply(
                                new BigDecimal("100")
                        );

        if (expenseRatio.compareTo(
                new BigDecimal("50")) <= 0) {

            return BUDGET_MAX;
        }

        if (expenseRatio.compareTo(
                new BigDecimal("70")) <= 0) {

            return 20;
        }

        if (expenseRatio.compareTo(
                new BigDecimal("85")) <= 0) {

            return 15;
        }

        if (expenseRatio.compareTo(
                new BigDecimal("100")) <= 0) {

            return 8;
        }

        return 0;
    }

    /*
     * ============================================================
     * Spending behavior component
     * ============================================================
     *
     * Maximum: 20 points
     *
     * Considers:
     * - failed transactions
     * - high-value transactions
     */
    private int calculateSpendingScore(
            TransactionAnalyticsResponse analytics,
            SpendingPatternResponse spendingPattern) {

        int score = SPENDING_MAX;

        long totalTransactions =
                analytics.getTotalTransactions();

        long failedTransactions =
                analytics.getFailedTransactions();

        if (totalTransactions > 0) {

            BigDecimal failedRatio =
                    BigDecimal.valueOf(
                            failedTransactions
                    )
                    .divide(
                            BigDecimal.valueOf(
                                    totalTransactions
                            ),
                            4,
                            RoundingMode.HALF_UP
                    )
                    .multiply(
                            new BigDecimal("100")
                    );

            if (failedRatio.compareTo(
                    new BigDecimal("20")) >= 0) {

                score -= 10;

            } else if (failedRatio.compareTo(
                    new BigDecimal("10")) >= 0) {

                score -= 6;

            } else if (failedRatio.compareTo(
                    new BigDecimal("5")) >= 0) {

                score -= 3;
            }
        }

        long highValueTransactions =
                spendingPattern
                        .getHighValueTransactionCount();

        if (highValueTransactions >= 5) {

            score -= 5;

        } else if (highValueTransactions >= 3) {

            score -= 3;

        } else if (highValueTransactions >= 1) {

            score -= 1;
        }

        return clamp(
                score,
                0,
                SPENDING_MAX
        );
    }

    /*
     * ============================================================
     * Spending concentration component
     * ============================================================
     *
     * Maximum: 15 points
     */
    private int calculateConcentrationScore(
            SpendingPatternResponse spendingPattern) {

        double concentration =
                spendingPattern
                        .getTopCategoryPercentage();

        if (concentration <= 20) {
            return CONCENTRATION_MAX;
        }

        if (concentration <= 30) {
            return 12;
        }

        if (concentration <= 50) {
            return 8;
        }

        if (concentration <= 70) {
            return 4;
        }

        return 0;
    }

    /*
     * ============================================================
     * Risk component
     * ============================================================
     *
     * Maximum: 15 points
     *
     * Failed transaction ratio is used
     * as a deterministic backend signal.
     */
    private int calculateRiskScore(
            TransactionAnalyticsResponse analytics) {

        long totalTransactions =
                analytics.getTotalTransactions();

        long failedTransactions =
                analytics.getFailedTransactions();

        if (totalTransactions <= 0) {
            return RISK_MAX;
        }

        BigDecimal failedRatio =
                BigDecimal.valueOf(
                        failedTransactions
                )
                .divide(
                        BigDecimal.valueOf(
                                totalTransactions
                        ),
                        4,
                        RoundingMode.HALF_UP
                )
                .multiply(
                        new BigDecimal("100")
                );

        if (failedRatio.compareTo(
                BigDecimal.ZERO) <= 0) {

            return RISK_MAX;
        }

        if (failedRatio.compareTo(
                new BigDecimal("2")) <= 0) {

            return 12;
        }

        if (failedRatio.compareTo(
                new BigDecimal("5")) <= 0) {

            return 9;
        }

        if (failedRatio.compareTo(
                new BigDecimal("10")) <= 0) {

            return 5;
        }

        return 0;
    }

    private String determineHealthLevel(
            int score) {

        if (score >= 80) {
            return "EXCELLENT";
        }

        if (score >= 65) {
            return "GOOD";
        }

        if (score >= 50) {
            return "FAIR";
        }

        if (score >= 35) {
            return "NEEDS_ATTENTION";
        }

        return "CRITICAL";
    }

    private String buildSummary(
            int score,
            String level) {

        return "Financial health score is "
                + score
                + "/100 with health level "
                + level
                + ".";
    }

    private BigDecimal safeValue(
            BigDecimal value) {

        return value == null
                ? BigDecimal.ZERO
                : value;
    }

    private int clamp(
            int value,
            int min,
            int max) {

        return Math.max(
                min,
                Math.min(
                        value,
                        max
                )
        );
    }

    private void validateContext(
            FinancialIntelligenceContext context) {

        if (context == null) {
            throw new IllegalArgumentException(
                    "Financial intelligence context cannot be null"
            );
        }

        if (context.getAnalytics() == null) {
            throw new IllegalStateException(
                    "Transaction analytics unavailable"
            );
        }

        if (context.getBudgetAdvisor() == null) {
            throw new IllegalStateException(
                    "Budget advisor data unavailable"
            );
        }

        if (context.getSpendingPattern() == null) {
            throw new IllegalStateException(
                    "Spending pattern data unavailable"
            );
        }
    }
}