package com.smartwallet.service;

import com.smartwallet.dto.FinancialHealthScoreResponse;
import com.smartwallet.dto.FinancialIntelligenceContext;
import com.smartwallet.dto.SmartInsightResponse;
import com.smartwallet.dto.SpendingPatternResponse;
import com.smartwallet.dto.TransactionAnalyticsResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class SmartInsightService {

    private static final int MAX_INSIGHTS = 5;

    public List<SmartInsightResponse> generateInsights(
            FinancialIntelligenceContext context) {

        validateContext(context);

        List<SmartInsightResponse> insights =
                new ArrayList<>();

        TransactionAnalyticsResponse analytics =
                context.getAnalytics();

        SpendingPatternResponse spendingPattern =
                context.getSpendingPattern();

        FinancialHealthScoreResponse healthScore =
                context.getHealthScore();

        /*
         * 1. Savings insight
         */
        addSavingsInsight(
                analytics,
                insights
        );

        /*
         * 2. Budget / expense insight
         */
        addExpenseInsight(
                analytics,
                insights
        );

        /*
         * 3. Spending concentration insight
         */
        addConcentrationInsight(
                spendingPattern,
                insights
        );

        /*
         * 4. Failed transaction risk insight
         */
        addFailedTransactionInsight(
                analytics,
                insights
        );

        /*
         * 5. Overall financial health insight
         */
        addHealthInsight(
                healthScore,
                insights
        );

        /*
         * Production response limit.
         */
        if (insights.size() > MAX_INSIGHTS) {

            return new ArrayList<>(
                    insights.subList(
                            0,
                            MAX_INSIGHTS
                    )
            );
        }

        return insights;
    }

    private void addSavingsInsight(
            TransactionAnalyticsResponse analytics,
            List<SmartInsightResponse> insights) {

        BigDecimal savingsRatio =
                safeValue(
                        analytics.getSavingsRatio()
                );

        BigDecimal savings =
                safeValue(
                        analytics.getSavings()
                );

        if (savingsRatio.compareTo(
                new BigDecimal("20")) >= 0) {

            insights.add(
                    new SmartInsightResponse(
                            "SAVINGS",
                            "POSITIVE",
                            "Strong Savings Rate",
                            "Your current savings rate is "
                                    + savingsRatio
                                    + "%.",
                            "Continue maintaining your current "
                                    + "saving discipline."
                    )
            );

            return;
        }

        if (savingsRatio.compareTo(
                BigDecimal.ZERO) < 0) {

            insights.add(
                    new SmartInsightResponse(
                            "SAVINGS",
                            "CRITICAL",
                            "Negative Savings",
                            "Your expenses currently exceed "
                                    + "your income by ₹"
                                    + savings.abs()
                                    + ".",
                            "Reduce discretionary spending and "
                                    + "review your monthly budget."
                    )
            );

            return;
        }

        if (savingsRatio.compareTo(
                new BigDecimal("10")) < 0) {

            insights.add(
                    new SmartInsightResponse(
                            "SAVINGS",
                            "WARNING",
                            "Low Savings Rate",
                            "Your current savings rate is "
                                    + savingsRatio
                                    + "%.",
                            "Look for opportunities to increase "
                                    + "monthly savings."
                    )
            );
        }
    }

    private void addExpenseInsight(
            TransactionAnalyticsResponse analytics,
            List<SmartInsightResponse> insights) {

        BigDecimal income =
                safeValue(
                        analytics.getTotalIncome()
                );

        BigDecimal expense =
                safeValue(
                        analytics.getTotalExpense()
                );

        if (income.compareTo(
                BigDecimal.ZERO) <= 0) {

            return;
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
                new BigDecimal("100")) > 0) {

            insights.add(
                    new SmartInsightResponse(
                            "BUDGET",
                            "CRITICAL",
                            "Expenses Exceed Income",
                            "Your expenses are "
                                    + expenseRatio
                                    + "% of your income.",
                            "Reduce expenses and review "
                                    + "non-essential spending."
                    )
            );

            return;
        }

        if (expenseRatio.compareTo(
                new BigDecimal("85")) >= 0) {

            insights.add(
                    new SmartInsightResponse(
                            "BUDGET",
                            "WARNING",
                            "High Expense Ratio",
                            "Your expenses are "
                                    + expenseRatio
                                    + "% of your income.",
                            "Review discretionary expenses "
                                    + "to create more savings."
                    )
            );
        }
    }

    private void addConcentrationInsight(
            SpendingPatternResponse spendingPattern,
            List<SmartInsightResponse> insights) {

        double concentration =
                spendingPattern
                        .getTopCategoryPercentage();

        String category =
                spendingPattern
                        .getTopCategory();

        if (concentration >= 70) {

            insights.add(
                    new SmartInsightResponse(
                            "SPENDING_PATTERN",
                            "WARNING",
                            "Highly Concentrated Spending",
                            "A large portion of your spending "
                                    + "is concentrated in "
                                    + safeCategory(category)
                                    + " ("
                                    + concentration
                                    + "%).",
                            "Review this category and determine "
                                    + "whether some spending can "
                                    + "be reduced."
                    )
            );

            return;
        }

        if (concentration >= 50) {

            insights.add(
                    new SmartInsightResponse(
                            "SPENDING_PATTERN",
                            "MEDIUM",
                            "Concentrated Spending",
                            safeCategory(category)
                                    + " represents "
                                    + concentration
                                    + "% of your spending.",
                            "Monitor this category closely "
                                    + "during your next budget cycle."
                    )
            );
        }
    }

    private void addFailedTransactionInsight(
            TransactionAnalyticsResponse analytics,
            List<SmartInsightResponse> insights) {

        long total =
                analytics.getTotalTransactions();

        long failed =
                analytics.getFailedTransactions();

        if (total <= 0 || failed <= 0) {
            return;
        }

        BigDecimal failedRatio =
                BigDecimal.valueOf(failed)
                        .divide(
                                BigDecimal.valueOf(total),
                                4,
                                RoundingMode.HALF_UP
                        )
                        .multiply(
                                new BigDecimal("100")
                        );

        if (failedRatio.compareTo(
                new BigDecimal("10")) >= 0) {

            insights.add(
                    new SmartInsightResponse(
                            "TRANSACTION_RISK",
                            "WARNING",
                            "High Failed Transaction Rate",
                            failedRatio
                                    + "% of your transactions "
                                    + "have failed.",
                            "Review recent failed transactions "
                                    + "and investigate recurring "
                                    + "payment issues."
                    )
            );
        }
    }

    private void addHealthInsight(
            FinancialHealthScoreResponse healthScore,
            List<SmartInsightResponse> insights) {

        int score =
                healthScore.getScore();

        String level =
                healthScore.getLevel();

        if (score < 35) {

            insights.add(
                    new SmartInsightResponse(
                            "FINANCIAL_HEALTH",
                            "CRITICAL",
                            "Financial Health Needs Attention",
                            "Your financial health score is "
                                    + score
                                    + "/100.",
                            "Review your expenses, savings, "
                                    + "and spending patterns."
                    )
            );

            return;
        }

        if (score >= 80) {

            insights.add(
                    new SmartInsightResponse(
                            "FINANCIAL_HEALTH",
                            "POSITIVE",
                            "Healthy Financial Position",
                            "Your financial health score is "
                                    + score
                                    + "/100 ("
                                    + level
                                    + ").",
                            "Continue monitoring your spending "
                                    + "and maintaining your savings."
                    )
            );
        }
    }

    private BigDecimal safeValue(
            BigDecimal value) {

        return value == null
                ? BigDecimal.ZERO
                : value;
    }

    private String safeCategory(
            String category) {

        if (category == null
                || category.isBlank()) {

            return "your top spending category";
        }

        return category;
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

        if (context.getSpendingPattern() == null) {

            throw new IllegalStateException(
                    "Spending pattern data unavailable"
            );
        }

        if (context.getHealthScore() == null) {

            throw new IllegalStateException(
                    "Financial health score unavailable"
            );
        }
    }
}