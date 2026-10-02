package com.smartwallet.service;

import com.smartwallet.dto.BudgetAdvisorResponse;
import com.smartwallet.dto.CategorySpendingResponse;
import com.smartwallet.dto.FinancialHealthScoreResponse;
import com.smartwallet.dto.FinancialIntelligenceContext;
import com.smartwallet.dto.FinancialIntelligenceResponse;
import com.smartwallet.dto.SmartInsightResponse;
import com.smartwallet.dto.SpendingPatternResponse;
import com.smartwallet.dto.TransactionAnalyticsResponse;
import com.smartwallet.dto.UnifiedAIInsightResponse;
import com.smartwallet.exception.AIServiceException;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FinancialIntelligenceService {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    FinancialIntelligenceService.class
            );

    private final TransactionAnalyticsService transactionAnalyticsService;
    private final CategoryAnalysisService categoryAnalysisService;
    private final SpendingPatternService spendingPatternService;
    private final BudgetAdvisorService budgetAdvisorService;
    private final FinancialHealthScoreService financialHealthScoreService;
    private final SmartInsightService smartInsightService;
    private final AIService aiService;

    public FinancialIntelligenceService(
            TransactionAnalyticsService transactionAnalyticsService,
            CategoryAnalysisService categoryAnalysisService,
            SpendingPatternService spendingPatternService,
            BudgetAdvisorService budgetAdvisorService,
            FinancialHealthScoreService financialHealthScoreService,
            SmartInsightService smartInsightService,
            AIService aiService) {

        this.transactionAnalyticsService =
                transactionAnalyticsService;

        this.categoryAnalysisService =
                categoryAnalysisService;

        this.spendingPatternService =
                spendingPatternService;

        this.budgetAdvisorService =
                budgetAdvisorService;

        this.financialHealthScoreService =
                financialHealthScoreService;

        this.smartInsightService =
                smartInsightService;

        this.aiService =
                aiService;
    }

    /*
     * ============================================================
     * BUILD FINANCIAL INTELLIGENCE CONTEXT
     * ============================================================
     *
     * This method builds trusted deterministic financial data.
     *
     * AI is NOT involved in calculating the financial health score.
     */

    public FinancialIntelligenceContext buildContext(
            String email) {

        TransactionAnalyticsResponse analytics =
                transactionAnalyticsService
                        .getTransactionAnalytics(email);

        List<CategorySpendingResponse> categories =
                categoryAnalysisService
                        .getCategorySpending(email);

        SpendingPatternResponse spendingPattern =
                spendingPatternService
                        .getSpendingPattern(email);

        BudgetAdvisorResponse budgetAdvisor =
                budgetAdvisorService
                        .getBudgetAdvisor(email);

        validateContext(
                analytics,
                categories,
                spendingPattern,
                budgetAdvisor
        );

        /*
         * Build the base trusted financial context.
         */
        FinancialIntelligenceContext baseContext =
                new FinancialIntelligenceContext(
                        analytics,
                        categories,
                        spendingPattern,
                        budgetAdvisor,
                        null
                );

        /*
         * Calculate deterministic financial health score.
         *
         * The score is calculated by backend rules,
         * not by Gemini.
         */
        FinancialHealthScoreResponse healthScore =
                financialHealthScoreService
                        .calculateScore(baseContext);

        /*
         * Build the final unified intelligence context.
         */
        return new FinancialIntelligenceContext(
                analytics,
                categories,
                spendingPattern,
                budgetAdvisor,
                healthScore
        );
    }

    /*
     * ============================================================
     * GET UNIFIED FINANCIAL INTELLIGENCE
     * ============================================================
     *
     * Production flow:
     *
     * Deterministic financial intelligence
     *              ↓
     * Smart insights
     *              ↓
     * Optional Gemini explanation
     *              ↓
     * Unified response
     *
     * Gemini failure must NOT break deterministic intelligence.
     */

    public FinancialIntelligenceResponse
            getFinancialIntelligence(String email) {

        logger.info(
                "Generating unified financial intelligence for user: {}",
                email
        );

        FinancialIntelligenceContext context =
                buildContext(email);

        /*
         * Generate deterministic smart insights.
         *
         * These insights remain available even if
         * the AI provider is unavailable.
         */
        List<SmartInsightResponse> insights =
                smartInsightService
                        .generateInsights(context);

        /*
         * AI is an optional explanation layer.
         *
         * If Gemini fails, the deterministic intelligence
         * response will still be returned successfully.
         */
        UnifiedAIInsightResponse aiInsight = null;

        try {

            aiInsight =
                    aiService.getUnifiedAIInsight(
                            email,
                            context,
                            insights
                    );

            logger.info(
                    "Unified AI insight generated successfully for user: {}",
                    email
            );

        } catch (AIServiceException ex) {

            logger.warn(
                    "Unified AI insight unavailable for user: {}. "
                    + "Returning deterministic financial intelligence.",
                    email
            );
        }

        /*
         * Return deterministic intelligence plus
         * optional AI explanation.
         */
        return new FinancialIntelligenceResponse(
                context.getAnalytics(),
                context.getCategories(),
                context.getSpendingPattern(),
                context.getBudgetAdvisor(),
                context.getHealthScore(),
                insights,
                aiInsight
        );
    }

    /*
     * ============================================================
     * CONTEXT VALIDATION
     * ============================================================
     */

    private void validateContext(
            TransactionAnalyticsResponse analytics,
            List<CategorySpendingResponse> categories,
            SpendingPatternResponse spendingPattern,
            BudgetAdvisorResponse budgetAdvisor) {

        if (analytics == null) {
            throw new IllegalStateException(
                    "Financial analytics unavailable"
            );
        }

        if (categories == null) {
            throw new IllegalStateException(
                    "Category spending data unavailable"
            );
        }

        if (spendingPattern == null) {
            throw new IllegalStateException(
                    "Spending pattern data unavailable"
            );
        }

        if (budgetAdvisor == null) {
            throw new IllegalStateException(
                    "Budget advisor data unavailable"
            );
        }
    }
}
