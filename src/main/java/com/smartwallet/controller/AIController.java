package com.smartwallet.controller;

import com.smartwallet.dto.AIInsightResponse;
import com.smartwallet.dto.BudgetHealthResponse;
import com.smartwallet.dto.FinancialScoreResponse;
import com.smartwallet.dto.GoalRecommendationResponse;
import com.smartwallet.service.AIService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

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

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @Operation(
            summary = "Generate AI Insights",
            description = "Returns AI-generated financial insights for the authenticated user."
    )
    @GetMapping("/insights")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public AIInsightResponse getAIInsights(Authentication authentication) {

        String email = authentication.getName();

        return aiService.getAIInsights(email);
    }

    @Operation(
            summary = "Financial Score",
            description = "Calculates the user's financial score, financial rating, risk level, and personalized remark."
    )
    @GetMapping("/financial-score")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public FinancialScoreResponse getFinancialScore(Authentication authentication) {

        return aiService.getFinancialScore(authentication.getName());
    }

    @Operation(
            summary = "Budget Health",
            description = "Returns budget analysis including total income, total expenses, remaining budget, health percentage, and overall budget status."
    )
    @GetMapping("/budget-health")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public BudgetHealthResponse getBudgetHealth(Authentication authentication) {

        return aiService.getBudgetHealth(authentication.getName());
    }

    @Operation(
            summary = "Goal Recommendation",
            description = "Returns AI-generated financial goal recommendations including progress and personalized suggestions."
    )
    @GetMapping("/goals")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public GoalRecommendationResponse getGoalRecommendation(Authentication authentication) {

        return aiService.getGoalRecommendation(authentication.getName());
    }
}