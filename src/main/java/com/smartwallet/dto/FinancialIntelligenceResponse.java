package com.smartwallet.dto;

import java.util.List;

public class FinancialIntelligenceResponse {

    private final TransactionAnalyticsResponse analytics;
    private final List<CategorySpendingResponse> categories;
    private final SpendingPatternResponse spendingPattern;
    private final BudgetAdvisorResponse budgetAdvisor;
    private final FinancialHealthScoreResponse healthScore;
    private final List<SmartInsightResponse> insights;
    private final UnifiedAIInsightResponse aiInsight;

    public FinancialIntelligenceResponse(
            TransactionAnalyticsResponse analytics,
            List<CategorySpendingResponse> categories,
            SpendingPatternResponse spendingPattern,
            BudgetAdvisorResponse budgetAdvisor,
            FinancialHealthScoreResponse healthScore,
            List<SmartInsightResponse> insights,
            UnifiedAIInsightResponse aiInsight) {

        this.analytics = analytics;
        this.categories = categories;
        this.spendingPattern = spendingPattern;
        this.budgetAdvisor = budgetAdvisor;
        this.healthScore = healthScore;
        this.insights = insights;
        this.aiInsight = aiInsight;
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

    public List<SmartInsightResponse> getInsights() {
        return insights;
    }

    public UnifiedAIInsightResponse getAiInsight() {
        return aiInsight;
    }
}