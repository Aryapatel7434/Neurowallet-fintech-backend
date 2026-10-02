package com.smartwallet.service;

import com.smartwallet.dto.BudgetAdvisorResponse;
import com.smartwallet.dto.CategorySpendingResponse;
import com.smartwallet.dto.FinancialHealthScoreResponse;
import com.smartwallet.dto.FinancialIntelligenceContext;
import com.smartwallet.dto.SpendingPatternResponse;
import com.smartwallet.dto.TransactionAnalyticsResponse;

import java.math.BigDecimal;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FinancialHealthScoreServiceTest {

    private final FinancialHealthScoreService service =
            new FinancialHealthScoreService();

    @Test
    void shouldCalculateHealthyFinancialScore() {

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
                new BudgetAdvisorResponse(
                        new BigDecimal("50000"),
                        new BigDecimal("30000"),
                        new BigDecimal("20000"),
                        new BigDecimal("10000"),
                        new BigDecimal("25000"),
                        new BigDecimal("15000"),
                        new BigDecimal("20000"),
                        new BigDecimal("10000"),
                        "NORMAL",
                        "Healthy budget",
                        Collections.emptyList()
                );

        SpendingPatternResponse pattern =
                new SpendingPatternResponse(
                        new BigDecimal("30000"),
                        new BigDecimal("3000"),
                        new BigDecimal("8000"),
                        "FOOD",
                        new BigDecimal("6000"),
                        20.0,
                        5,
                        1,
                        "DISTRIBUTED"
                );

        FinancialIntelligenceContext context =
                new FinancialIntelligenceContext(
                        analytics,
                        Collections.<CategorySpendingResponse>emptyList(),
                        pattern,
                        budget,
                        null
                );

        FinancialHealthScoreResponse response =
                service.calculateScore(context);

        assertNotNull(response);

        assertTrue(
                response.getScore() >= 80,
                "Healthy financial profile should score >= 80"
        );

        assertEquals(
                "EXCELLENT",
                response.getLevel()
        );
    }

    @Test
    void shouldHandleNegativeSavings() {

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
                new BudgetAdvisorResponse(
                        new BigDecimal("30000"),
                        new BigDecimal("40000"),
                        new BigDecimal("-10000"),
                        new BigDecimal("6000"),
                        new BigDecimal("15000"),
                        new BigDecimal("9000"),
                        new BigDecimal("25000"),
                        new BigDecimal("15000"),
                        "CRITICAL",
                        "Expenses exceed income",
                        Collections.emptyList()
                );

        SpendingPatternResponse pattern =
                new SpendingPatternResponse(
                        new BigDecimal("40000"),
                        new BigDecimal("4000"),
                        new BigDecimal("9000"),
                        "SHOPPING",
                        new BigDecimal("30000"),
                        75.0,
                        2,
                        5,
                        "HIGH"
                );

        FinancialIntelligenceContext context =
                new FinancialIntelligenceContext(
                        analytics,
                        Collections.<CategorySpendingResponse>emptyList(),
                        pattern,
                        budget,
                        null
                );

        FinancialHealthScoreResponse response =
                service.calculateScore(context);

        assertNotNull(response);

        assertEquals(
                0,
                response.getSavingsScore()
        );

        assertTrue(
                response.getScore() < 50,
                "Negative savings should significantly reduce health score"
        );
    }

    @Test
    void shouldHandleNoIncome() {

        TransactionAnalyticsResponse analytics =
                new TransactionAnalyticsResponse(
                        5,
                        5,
                        0,
                        BigDecimal.ZERO,
                        new BigDecimal("5000"),
                        BigDecimal.ZERO,
                        new BigDecimal("1000"),
                        new BigDecimal("2000"),
                        new BigDecimal("-5000"),
                        BigDecimal.ZERO
                );

        BudgetAdvisorResponse budget =
                new BudgetAdvisorResponse(
                        BigDecimal.ZERO,
                        new BigDecimal("5000"),
                        new BigDecimal("-5000"),
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        new BigDecimal("5000"),
                        BigDecimal.ZERO,
                        "CRITICAL",
                        "No income detected",
                        Collections.emptyList()
                );

        SpendingPatternResponse pattern =
                new SpendingPatternResponse(
                        new BigDecimal("5000"),
                        new BigDecimal("1000"),
                        new BigDecimal("2000"),
                        "FOOD",
                        new BigDecimal("4000"),
                        80.0,
                        1,
                        1,
                        "HIGH"
                );

        FinancialIntelligenceContext context =
                new FinancialIntelligenceContext(
                        analytics,
                        Collections.<CategorySpendingResponse>emptyList(),
                        pattern,
                        budget,
                        null
                );

        FinancialHealthScoreResponse response =
                service.calculateScore(context);

        assertNotNull(response);

        assertEquals(
                0,
                response.getSavingsScore()
        );

        assertEquals(
                0,
                response.getBudgetScore()
        );
    }
}