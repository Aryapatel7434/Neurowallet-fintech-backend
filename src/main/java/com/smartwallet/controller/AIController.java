package com.smartwallet.controller;

import com.smartwallet.dto.AIInsightResponse;
import com.smartwallet.dto.BudgetHealthResponse;
import com.smartwallet.dto.CategorySpendingResponse;
import com.smartwallet.dto.FinancialAnalysisResponse;
import com.smartwallet.dto.FinancialScoreResponse;
import com.smartwallet.dto.GoalRecommendationResponse;
import com.smartwallet.dto.SpendingPatternResponse;
import com.smartwallet.dto.TransactionAnalyticsResponse;

import com.smartwallet.service.AIService;
import com.smartwallet.service.CategoryAnalysisService;
import com.smartwallet.service.SpendingPatternService;
import com.smartwallet.service.TransactionAnalyticsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "AI APIs",
        description = "AI-powered financial analysis and recommendation endpoints"
)
@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final AIService aiService;
    private final TransactionAnalyticsService transactionAnalyticsService;
    private final CategoryAnalysisService categoryAnalysisService;
    private final SpendingPatternService spendingPatternService;

    public AIController(
            AIService aiService,
            TransactionAnalyticsService transactionAnalyticsService,
            CategoryAnalysisService categoryAnalysisService,
            SpendingPatternService spendingPatternService) {

        this.aiService = aiService;

        this.transactionAnalyticsService =
                transactionAnalyticsService;

        this.categoryAnalysisService =
                categoryAnalysisService;

        this.spendingPatternService =
                spendingPatternService;
    }

    // ============================================================
    // AI INSIGHTS
    // ============================================================

    @Operation(
            summary = "Generate AI Insights",
            description = "Returns AI-generated financial insights for the authenticated user."
    )
    @GetMapping("/insights")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public AIInsightResponse getAIInsights(
            Authentication authentication) {

        return aiService.getAIInsights(
                authentication.getName()
        );
    }

    // ============================================================
    // FINANCIAL SCORE
    // ============================================================

    @Operation(
            summary = "Financial Score",
            description = "Calculates the user's financial score, rating, risk level and personalized remark."
    )
    @GetMapping("/financial-score")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public FinancialScoreResponse getFinancialScore(
            Authentication authentication) {

        return aiService.getFinancialScore(
                authentication.getName()
        );
    }

    // ============================================================
    // BUDGET HEALTH
    // ============================================================

    @Operation(
            summary = "Budget Health",
            description = "Returns budget analysis including income, expenses, savings and budget health."
    )
    @GetMapping("/budget-health")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public BudgetHealthResponse getBudgetHealth(
            Authentication authentication) {

        return aiService.getBudgetHealth(
                authentication.getName()
        );
    }

    // ============================================================
    // GOAL RECOMMENDATION
    // ============================================================

    @Operation(
            summary = "Goal Recommendation",
            description = "Returns AI-generated financial goal recommendations."
    )
    @GetMapping("/goals")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public GoalRecommendationResponse getGoalRecommendation(
            Authentication authentication) {

        return aiService.getGoalRecommendation(
                authentication.getName()
        );
    }

    // ============================================================
    // TRANSACTION ANALYTICS
    // ============================================================

    @Operation(
            summary = "Transaction Analytics",
            description = "Returns deterministic financial analytics calculated from the authenticated user's transactions."
    )
    @GetMapping("/transaction-analytics")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public TransactionAnalyticsResponse getTransactionAnalytics(
            Authentication authentication) {

        return transactionAnalyticsService
                .getTransactionAnalytics(
                        authentication.getName()
                );
    }

    // ============================================================
    // CATEGORY SPENDING
    // ============================================================

    @Operation(
            summary = "Category Spending Analysis",
            description = "Returns spending distribution across transaction categories."
    )
    @GetMapping("/category-spending")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<CategorySpendingResponse> getCategorySpending(
            Authentication authentication) {

        return categoryAnalysisService
                .getCategorySpending(
                        authentication.getName()
                );
    }

    // ============================================================
    // SPENDING PATTERN
    // ============================================================

    @Operation(
            summary = "Spending Pattern Analysis",
            description = "Analyzes the authenticated user's spending behavior and concentration."
    )
    @GetMapping("/spending-pattern")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public SpendingPatternResponse getSpendingPattern(
            Authentication authentication) {

        return spendingPatternService
                .getSpendingPattern(
                        authentication.getName()
                );
    }

    // ============================================================
    // AI FINANCIAL ANALYSIS
    // ============================================================

    @Operation(
            summary = "AI Financial Analysis",
            description = "Generates an AI-powered financial analysis using transaction analytics, category spending and spending patterns."
    )
    @GetMapping("/financial-analysis")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public FinancialAnalysisResponse getFinancialAnalysis(
            Authentication authentication) {

        return aiService.getFinancialAnalysis(
                authentication.getName()
        );
    }
}