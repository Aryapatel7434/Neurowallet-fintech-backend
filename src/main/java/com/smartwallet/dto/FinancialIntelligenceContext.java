package com.smartwallet.dto;

import java.util.List;

public class FinancialIntelligenceContext {

    private final TransactionAnalyticsResponse analytics;
    private final List<CategorySpendingResponse> categories;
    private final SpendingPatternResponse spendingPattern;
    private final BudgetAdvisorResponse budgetAdvisor;
    private final FinancialHealthScoreResponse healthScore;

    public FinancialIntelligenceContext(
            TransactionAnalyticsResponse analytics,
            List<CategorySpendingResponse> categories,
            SpendingPatternResponse spendingPattern,
            BudgetAdvisorResponse budgetAdvisor,
            FinancialHealthScoreResponse healthScore) {

        this.analytics = analytics;
        this.categories = categories;
        this.spendingPattern = spendingPattern;
        this.budgetAdvisor = budgetAdvisor;
        this.healthScore = healthScore;
    }

    public TransactionAnalyticsResponse getAnalytics() {
        return analytics;
    }

    public List<CategorySpendingResponse> getCategories() {
        return categories;
    }

    public SpendingPatternResponse getSpendingPattern() {
        return spendingPattern;
    }

    public BudgetAdvisorResponse getBudgetAdvisor() {
        return budgetAdvisor;
    }

    public FinancialHealthScoreResponse getHealthScore() {
        return healthScore;
    }
}