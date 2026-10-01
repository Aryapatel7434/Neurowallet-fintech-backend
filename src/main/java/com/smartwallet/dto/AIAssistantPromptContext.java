package com.smartwallet.dto;

import java.util.List;

public class AIAssistantPromptContext {

    private final TransactionAnalyticsResponse analytics;
    private final List<CategorySpendingResponse> categories;
    private final BudgetAdvisorResponse budgetAdvisor;

    public AIAssistantPromptContext(
            TransactionAnalyticsResponse analytics,
            List<CategorySpendingResponse> categories,
            BudgetAdvisorResponse budgetAdvisor) {

        this.analytics = analytics;
        this.categories = categories;
        this.budgetAdvisor = budgetAdvisor;
    }

    public TransactionAnalyticsResponse getAnalytics() {
        return analytics;
    }

    public List<CategorySpendingResponse> getCategories() {
        return categories;
    }

    public BudgetAdvisorResponse getBudgetAdvisor() {
        return budgetAdvisor;
    }
}