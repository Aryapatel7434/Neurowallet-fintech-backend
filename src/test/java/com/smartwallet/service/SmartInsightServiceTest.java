package com.smartwallet.service;

import com.smartwallet.dto.BudgetAdvisorResponse;
import com.smartwallet.dto.CategorySpendingResponse;
import com.smartwallet.dto.FinancialHealthScoreResponse;
import com.smartwallet.dto.FinancialIntelligenceContext;
import com.smartwallet.dto.SmartInsightResponse;
import com.smartwallet.dto.SpendingPatternResponse;
import com.smartwallet.dto.TransactionAnalyticsResponse;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SmartInsightServiceTest {

    private final SmartInsightService service =
            new SmartInsightService();

    @Test
    void shouldDetectNegativeSavings() {

        TransactionAnalyticsResponse analytics =
                new TransactionAnalyticsResponse(
                        10,
                        10,
                        0,
                        new BigDecimal("30000"),
                        new BigDecimal("40000"),
                        BigDecimal.ZERO,
                        new BigDecimal("4000"),
                        new BigDecimal("9000"),
                        new BigDecimal("-10000"),
                        new BigDecimal("-33.33")
                );

        BudgetAdvisorResponse budget =
                createBudget(
                        "CRITICAL"
                );

        SpendingPatternResponse pattern =
                createPattern(
                        40.0,
                        "SHOPPING"
                );

        FinancialHealthScoreResponse healthScore =
                createHealthScore(
                        25,
                        "CRITICAL"
                );

        FinancialIntelligenceContext context =
                createContext(
                        analytics,
                        budget,
                        pattern,
                        healthScore
                );

        List<SmartInsightResponse> insights =
                service.generateInsights(context);

        assertFalse(insights.isEmpty());

        assertTrue(
                insights.stream()
                        .anyMatch(
                                insight ->
                                        "SAVINGS".equals(
                                                insight.getType()
                                        )
                        )
        );
    }

    @Test
    void shouldDetectHighExpenseRatio() {

        TransactionAnalyticsResponse analytics =
                new TransactionAnalyticsResponse(
                        10,
                        10,
                        0,
                        new BigDecimal("50000"),
                        new BigDecimal("45000"),
                        BigDecimal.ZERO,
                        new BigDecimal("4500"),
                        new BigDecimal("10000"),
                        new BigDecimal("5000"),
                        new BigDecimal("10")
                );

        BudgetAdvisorResponse budget =
                createBudget(
                        "HIGH"
                );

        SpendingPatternResponse pattern =
                createPattern(
                        30.0,
                        "FOOD"
                );

        FinancialHealthScoreResponse healthScore =
                createHealthScore(
                        60,
                        "FAIR"
                );

        FinancialIntelligenceContext context =
                createContext(
                        analytics,
                        budget,
                        pattern,
                        healthScore
                );

        List<SmartInsightResponse> insights =
                service.generateInsights(context);

        assertTrue(
                insights.stream()
                        .anyMatch(
                                insight ->
                                        "BUDGET".equals(
                                                insight.getType()
                                        )
                        )
        );
    }

    @Test
    void shouldDetectHighlyConcentratedSpending() {

        TransactionAnalyticsResponse analytics =
                new TransactionAnalyticsResponse(
                        10,
                        10,
                        0,
                        new BigDecimal("50000"),
                        new BigDecimal("30000"),
                        BigDecimal.ZERO,
                        new BigDecimal("3000"),
                        new BigDecimal("8000"),
                        new BigDecimal("20000"),
                        new BigDecimal("40")
                );

        BudgetAdvisorResponse budget =
                createBudget(
                        "NORMAL"
                );

        SpendingPatternResponse pattern =
                createPattern(
                        75.0,
                        "SHOPPING"
                );

        FinancialHealthScoreResponse healthScore =
                createHealthScore(
                        65,
                        "GOOD"
                );

        FinancialIntelligenceContext context =
                createContext(
                        analytics,
                        budget,
                        pattern,
                        healthScore
                );

        List<SmartInsightResponse> insights =
                service.generateInsights(context);

        assertTrue(
                insights.stream()
                        .anyMatch(
                                insight ->
                                        "SPENDING_PATTERN"
                                                .equals(
                                                        insight.getType()
                                                )
                        )
        );
    }

    @Test
    void shouldDetectHighFailedTransactionRate() {

        TransactionAnalyticsResponse analytics =
                new TransactionAnalyticsResponse(
                        10,
                        8,
                        2,
                        new BigDecimal("50000"),
                        new BigDecimal("30000"),
                        BigDecimal.ZERO,
                        new BigDecimal("3000"),
                        new BigDecimal("8000"),
                        new BigDecimal("20000"),
                        new BigDecimal("40")
                );

        BudgetAdvisorResponse budget =
                createBudget(
                        "NORMAL"
                );

        SpendingPatternResponse pattern =
                createPattern(
                        25.0,
                        "FOOD"
                );

        FinancialHealthScoreResponse healthScore =
                createHealthScore(
                        65,
                        "GOOD"
                );

        FinancialIntelligenceContext context =
                createContext(
                        analytics,
                        budget,
                        pattern,
                        healthScore
                );

        List<SmartInsightResponse> insights =
                service.generateInsights(context);

        assertTrue(
                insights.stream()
                        .anyMatch(
                                insight ->
                                        "TRANSACTION_RISK"
                                                .equals(
                                                        insight.getType()
                                                )
                        )
        );
    }

    @Test
    void shouldDetectHealthyFinancialPosition() {

        TransactionAnalyticsResponse analytics =
                new TransactionAnalyticsResponse(
                        10,
                        10,
                        0,
                        new BigDecimal("50000"),
                        new BigDecimal("25000"),
                        BigDecimal.ZERO,
                        new BigDecimal("2500"),
                        new BigDecimal("7000"),
                        new BigDecimal("25000"),
                        new BigDecimal("50")
                );

        BudgetAdvisorResponse budget =
                createBudget(
                        "NORMAL"
                );

        SpendingPatternResponse pattern =
                createPattern(
                        20.0,
                        "FOOD"
                );

        FinancialHealthScoreResponse healthScore =
                createHealthScore(
                        90,
                        "EXCELLENT"
                );

        FinancialIntelligenceContext context =
                createContext(
                        analytics,
                        budget,
                        pattern,
                        healthScore
                );

        List<SmartInsightResponse> insights =
                service.generateInsights(context);

        assertTrue(
                insights.stream()
                        .anyMatch(
                                insight ->
                                        "FINANCIAL_HEALTH"
                                                .equals(
                                                        insight.getType()
                                                )
                        )
        );
    }

    @Test
    void shouldLimitInsightsToMaximumFive() {

        TransactionAnalyticsResponse analytics =
                new TransactionAnalyticsResponse(
                        10,
                        8,
                        2,
                        new BigDecimal("30000"),
                        new BigDecimal("40000"),
                        BigDecimal.ZERO,
                        new BigDecimal("4000"),
                        new BigDecimal("15000"),
                        new BigDecimal("-10000"),
                        new BigDecimal("-33.33")
                );

        BudgetAdvisorResponse budget =
                createBudget(
                        "CRITICAL"
                );

        SpendingPatternResponse pattern =
                createPattern(
                        80.0,
                        "SHOPPING"
                );

        FinancialHealthScoreResponse healthScore =
                createHealthScore(
                        20,
                        "CRITICAL"
                );

        FinancialIntelligenceContext context =
                createContext(
                        analytics,
                        budget,
                        pattern,
                        healthScore
                );

        List<SmartInsightResponse> insights =
                service.generateInsights(context);

        assertTrue(
                insights.size() <= 5
        );
    }

    @Test
    void shouldRejectNullContext() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.generateInsights(null)
        );
    }

    private FinancialIntelligenceContext createContext(
            TransactionAnalyticsResponse analytics,
            BudgetAdvisorResponse budget,
            SpendingPatternResponse pattern,
            FinancialHealthScoreResponse healthScore) {

        return new FinancialIntelligenceContext(
                analytics,
                Collections.<CategorySpendingResponse>emptyList(),
                pattern,
                budget,
                healthScore
        );
    }

    private BudgetAdvisorResponse createBudget(
            String priority) {

        return new BudgetAdvisorResponse(
                new BigDecimal("50000"),
                new BigDecimal("30000"),
                new BigDecimal("20000"),
                new BigDecimal("10000"),
                new BigDecimal("25000"),
                new BigDecimal("15000"),
                new BigDecimal("20000"),
                new BigDecimal("10000"),
                priority,
                "Budget status",
                Collections.emptyList()
        );
    }

    private SpendingPatternResponse createPattern(
            double concentration,
            String category) {

        return new SpendingPatternResponse(
                new BigDecimal("30000"),
                new BigDecimal("3000"),
                new BigDecimal("8000"),
                category,
                new BigDecimal("10000"),
                concentration,
                5,
                2,
                concentration >= 50
                        ? "HIGH"
                        : "DISTRIBUTED"
        );
    }

    private FinancialHealthScoreResponse createHealthScore(
            int score,
            String level) {

        return new FinancialHealthScoreResponse(
                score,
                level,
                "Financial health summary",
                20,
                20,
                15,
                12,
                10
        );
    }
}