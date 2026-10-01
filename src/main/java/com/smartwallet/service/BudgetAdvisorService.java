package com.smartwallet.service;

import com.smartwallet.dto.BudgetAdvisorResponse;
import com.smartwallet.dto.CategorySpendingResponse;
import com.smartwallet.dto.TransactionAnalyticsResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class BudgetAdvisorService {

    private final TransactionAnalyticsService transactionAnalyticsService;
    private final CategoryAnalysisService categoryAnalysisService;

    public BudgetAdvisorService(
            TransactionAnalyticsService transactionAnalyticsService,
            CategoryAnalysisService categoryAnalysisService) {

        this.transactionAnalyticsService =
                transactionAnalyticsService;

        this.categoryAnalysisService =
                categoryAnalysisService;
    }

    public BudgetAdvisorResponse getBudgetAdvisor(
            String email) {

        TransactionAnalyticsResponse analytics =
                transactionAnalyticsService
                        .getTransactionAnalytics(email);

        BigDecimal income =
                safeValue(analytics.getTotalIncome());

        BigDecimal expense =
                safeValue(analytics.getTotalExpense());

        BigDecimal savings =
                safeValue(analytics.getSavings());

        List<CategorySpendingResponse> categories =
                categoryAnalysisService
                        .getCategorySpending(email);

        BigDecimal essentialExpense =
                BigDecimal.ZERO;

        BigDecimal discretionaryExpense =
                BigDecimal.ZERO;

        if (categories != null) {

            for (CategorySpendingResponse category : categories) {

                if (category == null
                        || category.getCategory() == null
                        || category.getAmount() == null) {

                    continue;
                }

                BigDecimal amount =
                        category.getAmount().abs();

                if (isEssentialCategory(
                        category.getCategory())) {

                    essentialExpense =
                            essentialExpense.add(amount);

                } else {

                    discretionaryExpense =
                            discretionaryExpense.add(amount);
                }
            }
        }

        /*
         * Baseline budget model.
         *
         * Gemini will later personalize the recommendations,
         * but these values are calculated deterministically
         * from backend financial data.
         */

        BigDecimal recommendedSavings =
                income
                        .multiply(new BigDecimal("0.20"))
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        BigDecimal essentialBudget =
                income
                        .multiply(new BigDecimal("0.50"))
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        BigDecimal discretionaryBudget =
                income
                        .multiply(new BigDecimal("0.30"))
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        String priority =
                determinePriority(
                        income,
                        savings
                );

        String summary =
                buildSummary(
                        income,
                        expense,
                        savings,
                        essentialExpense,
                        discretionaryExpense
                );

        List<String> recommendations =
                buildRecommendations(
                        income,
                        savings,
                        essentialExpense,
                        discretionaryExpense
                );

return new BudgetAdvisorResponse(
        income,
        expense,
        savings,
        recommendedSavings,
        essentialBudget,
        discretionaryBudget,
        essentialExpense,
        discretionaryExpense,
        priority,
        summary,
        recommendations
);
    }

    private boolean isEssentialCategory(
            String category) {

        return "RENT".equalsIgnoreCase(category)
                || "EMI".equalsIgnoreCase(category)
                || "BILLS".equalsIgnoreCase(category)
                || "FOOD".equalsIgnoreCase(category)
                || "HEALTH".equalsIgnoreCase(category)
                || "EDUCATION".equalsIgnoreCase(category);
    }

    private String determinePriority(
            BigDecimal income,
            BigDecimal savings) {

        if (income.compareTo(BigDecimal.ZERO) <= 0) {
            return "CRITICAL";
        }

        if (savings.compareTo(BigDecimal.ZERO) < 0) {
            return "HIGH";
        }

        BigDecimal savingsRatio =
                savings
                        .multiply(new BigDecimal("100"))
                        .divide(
                                income,
                                2,
                                RoundingMode.HALF_UP
                        );

        if (savingsRatio.compareTo(
                new BigDecimal("20")) < 0) {

            return "MEDIUM";
        }

        return "NORMAL";
    }

    private String buildSummary(
            BigDecimal income,
            BigDecimal expense,
            BigDecimal savings,
            BigDecimal essentialExpense,
            BigDecimal discretionaryExpense) {

        if (income.compareTo(BigDecimal.ZERO) <= 0) {

            return "No positive income was detected. "
                    + "Budget planning requires a positive income.";
        }

        if (savings.compareTo(BigDecimal.ZERO) < 0) {

            return "Expenses currently exceed income. "
                    + "The first priority should be reducing "
                    + "non-essential spending and restoring "
                    + "positive cash flow.";
        }

        return "Your current income is "
                + income
                + ", expenses are "
                + expense
                + ", and savings are "
                + savings
                + ". Essential spending is "
                + essentialExpense
                + " while discretionary spending is "
                + discretionaryExpense
                + ".";
    }

    private List<String> buildRecommendations(
            BigDecimal income,
            BigDecimal savings,
            BigDecimal essentialExpense,
            BigDecimal discretionaryExpense) {

        List<String> recommendations =
                new ArrayList<>();

        if (income.compareTo(BigDecimal.ZERO) <= 0) {

            recommendations.add(
                    "Maintain a positive and predictable income "
                    + "before setting a monthly budget."
            );

            return recommendations;
        }

        if (savings.compareTo(BigDecimal.ZERO) < 0) {

            recommendations.add(
                    "Reduce discretionary spending immediately."
            );

            recommendations.add(
                    "Review recurring bills and subscriptions "
                    + "for possible reductions."
            );

            recommendations.add(
                    "Focus on returning monthly cash flow "
                    + "to a positive value."
            );

            return recommendations;
        }

        BigDecimal savingsRatio =
                savings
                        .multiply(new BigDecimal("100"))
                        .divide(
                                income,
                                2,
                                RoundingMode.HALF_UP
                        );

        if (savingsRatio.compareTo(
                new BigDecimal("20")) < 0) {

            recommendations.add(
                    "Increase monthly savings toward "
                    + "at least 20% of income."
            );
        }

        if (discretionaryExpense.compareTo(
                income.multiply(new BigDecimal("0.30"))) > 0) {

            recommendations.add(
                    "Reduce discretionary spending toward "
                    + "the 30% budget target."
            );
        }

        if (essentialExpense.compareTo(
                income.multiply(new BigDecimal("0.50"))) > 0) {

            recommendations.add(
                    "Review essential expenses because they "
                    + "are above the 50% baseline."
            );
        }

        if (recommendations.isEmpty()) {

            recommendations.add(
                    "Maintain the current spending discipline "
                    + "and continue building savings."
            );
        }

        return recommendations;
    }

    private BigDecimal safeValue(
            BigDecimal value) {

        return value == null
                ? BigDecimal.ZERO
                : value;
    }
}