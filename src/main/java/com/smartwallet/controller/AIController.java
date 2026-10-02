package com.smartwallet.controller;

import com.smartwallet.dto.AIInsightResponse;
import com.smartwallet.dto.AIAssistantRequest;
import com.smartwallet.dto.AIAssistantResponse;
import com.smartwallet.dto.BudgetAdvisorResponse;
import com.smartwallet.dto.BudgetHealthResponse;
import com.smartwallet.dto.CategorySpendingResponse;
import com.smartwallet.dto.FinancialAnalysisResponse;
import com.smartwallet.dto.FinancialIntelligenceResponse;
import com.smartwallet.dto.FinancialScoreResponse;
import com.smartwallet.dto.FraudDetectionResponse;
import com.smartwallet.dto.GoalRecommendationResponse;
import com.smartwallet.dto.SpendingPatternResponse;
import com.smartwallet.dto.TransactionAnalyticsResponse;

import com.smartwallet.service.AIAssistantService;
import com.smartwallet.service.AIService;
import com.smartwallet.service.CategoryAnalysisService;
import com.smartwallet.service.FinancialIntelligenceService;
import com.smartwallet.service.FraudDetectionService;
import com.smartwallet.service.SpendingPatternService;
import com.smartwallet.service.TransactionAnalyticsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    private final AIAssistantService aiAssistantService;
    private final FraudDetectionService fraudDetectionService;
    private final FinancialIntelligenceService financialIntelligenceService;

    public AIController(
            AIService aiService,
            TransactionAnalyticsService transactionAnalyticsService,
            CategoryAnalysisService categoryAnalysisService,
            SpendingPatternService spendingPatternService,
            AIAssistantService aiAssistantService,
            FraudDetectionService fraudDetectionService,
            FinancialIntelligenceService financialIntelligenceService) {

        this.aiService = aiService;

        this.transactionAnalyticsService =
                transactionAnalyticsService;

        this.categoryAnalysisService =
                categoryAnalysisService;

        this.spendingPatternService =
                spendingPatternService;

        this.aiAssistantService =
                aiAssistantService;

        this.fraudDetectionService =
                fraudDetectionService;

        this.financialIntelligenceService =
                financialIntelligenceService;
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

    // ============================================================
    // AI BUDGET ADVISOR
    // ============================================================

    @Operation(
            summary = "AI Budget Advisor",
            description = "Generates a personalized AI-powered budget plan using the authenticated user's financial data."
    )
    @GetMapping("/budget-advisor")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public BudgetAdvisorResponse getBudgetAdvisor(
            Authentication authentication) {

        return aiService.getBudgetAdvisor(
                authentication.getName()
        );
    }

    // ============================================================
    // AI FINANCIAL ASSISTANT
    // ============================================================

    @Operation(
            summary = "AI Financial Assistant",
            description = "Answers financial questions using the authenticated user's trusted financial context."
    )
    @PostMapping("/assistant")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public AIAssistantResponse askAssistant(
            @Valid @RequestBody AIAssistantRequest request,
            Authentication authentication) {

        return aiAssistantService.askAssistant(
                authentication.getName(),
                request.getQuestion()
        );
    }

    // ============================================================
    // AI FRAUD DETECTION
    // ============================================================

    @Operation(
            summary = "AI Fraud Detection",
            description = "Analyzes a transaction using NeuroWallet's rule-based fraud detection engine and user behavioral history."
    )
    @GetMapping("/fraud/{transactionId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public FraudDetectionResponse detectFraud(
            @PathVariable Long transactionId,
            Authentication authentication) {

        return fraudDetectionService.detectFraud(
                transactionId,
                authentication.getName()
        );
    }

    // ============================================================
    // UNIFIED FINANCIAL INTELLIGENCE
    // ============================================================

    @Operation(
            summary = "Unified Financial Intelligence",
            description = "Returns a complete financial intelligence report including analytics, spending patterns, budget health, deterministic financial health score, smart insights and optional AI-generated explanation."
    )
    @GetMapping("/intelligence")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public FinancialIntelligenceResponse getFinancialIntelligence(
            Authentication authentication) {

        return financialIntelligenceService
                .getFinancialIntelligence(
                        authentication.getName()
                );
    }
}