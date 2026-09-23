package com.smartwallet.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.smartwallet.dto.AIInsightResponse;
import com.smartwallet.dto.BudgetHealthResponse;
import com.smartwallet.dto.FinancialContext;
import com.smartwallet.dto.FinancialScoreResponse;
import com.smartwallet.dto.FinancialSummary;
import com.smartwallet.dto.GoalRecommendationResponse;

import com.smartwallet.exception.AIServiceException;
import com.smartwallet.exception.WalletNotFoundException;

import com.smartwallet.model.Transaction;
import com.smartwallet.model.Wallet;

import com.smartwallet.repository.TransactionRepository;
import com.smartwallet.repository.WalletRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AIService {

    private static final Logger logger =
            LoggerFactory.getLogger(AIService.class);

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final AIPromptBuilder promptBuilder;
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public AIService(
            WalletRepository walletRepository,
            TransactionRepository transactionRepository,
            AIPromptBuilder promptBuilder,
            ChatClient.Builder chatClientBuilder,
            ObjectMapper objectMapper) {

        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.promptBuilder = promptBuilder;
        this.chatClient = chatClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    /*
     * ============================================================
     * AI INSIGHTS
     * ============================================================
     *
     * Flow:
     *
     * User
     *   ↓
     * Wallet + Transactions
     *   ↓
     * FinancialSummary
     *   ↓
     * FinancialContext
     *   ↓
     * PromptBuilder
     *   ↓
     * Gemini
     *   ↓
     * JSON
     *   ↓
     * AIInsightResponse
     */

    public AIInsightResponse getAIInsights(String email) {

        logger.info(
                "Generating AI insights for user: {}",
                email
        );

        Wallet wallet =
                walletRepository.findByUserEmail(email);

        if (wallet == null) {

            logger.error(
                    "Wallet not found for user: {}",
                    email
            );

            throw new WalletNotFoundException(
                    "Wallet not found for email: " + email
            );
        }

        List<Transaction> transactions =
                transactionRepository.findBySenderEmailOrReceiverEmail(
                        email,
                        email
                );

        FinancialSummary summary =
                calculateFinancialSummary(
                        wallet,
                        transactions,
                        email
                );

        /*
         * Convert deterministic financial data
         * into AI-safe financial context.
         */
        FinancialContext context =
                buildFinancialContext(summary);

        /*
         * Build the prompt for Gemini.
         */
        String prompt =
                promptBuilder.buildFinancialInsightPrompt(context);

        logger.debug(
                "Financial AI prompt prepared for user: {}",
                email
        );

        /*
         * Send financial context/prompt to Gemini.
         */
        String aiResponse;

        try {

            aiResponse = chatClient
                    .prompt()
                    .user(prompt)
                    .call()
                    .content();

        } catch (Exception e) {

            logger.error(
                    "AI provider failure while generating insights",
                    e
            );

            throw new AIServiceException(
                    "AI service is temporarily unavailable.",
                    e
            );
        }

        /*
         * Gemini returns JSON as String.
         *
         * Convert that JSON into our existing
         * AIInsightResponse DTO.
         */
        AIInsightResponse aiInsightResponse;

        try {

            if (aiResponse == null || aiResponse.isBlank()) {

                throw new AIServiceException(
                        "AI service returned an empty response."
                );
            }

            aiInsightResponse =
                    objectMapper.readValue(
                            aiResponse,
                            AIInsightResponse.class
                    );

            /*
             * Validate the structured AI response
             * exactly once.
             */
            validateAIInsightResponse(
                    aiInsightResponse
            );

        } catch (JsonProcessingException e) {

            logger.error(
                    "Invalid structured response received from AI",
                    e
            );

            throw new AIServiceException(
                    "AI service returned an invalid response.",
                    e
            );
        }

        logger.info(
                "AI insights generated successfully for user: {}",
                email
        );

        /*
         * Return the actual Gemini-generated
         * structured response.
         */
        return aiInsightResponse;
    }

    /*
     * ============================================================
     * BUDGET ALERT
     * ============================================================
     */

    private String calculateBudgetAlert(
            FinancialSummary summary) {

        BigDecimal savingsRatio =
                summary.getSavingsRatio();

        if (savingsRatio.compareTo(
                new BigDecimal("40")) >= 0) {

            return "Excellent budget management.";
        }

        if (savingsRatio.compareTo(
                new BigDecimal("20")) >= 0) {

            return "Your budget is healthy.";
        }

        if (savingsRatio.compareTo(
                BigDecimal.ZERO) >= 0) {

            return "Review discretionary spending.";
        }

        return "Warning: Your expenses exceed your income.";
    }

    /*
     * ============================================================
     * INVESTMENT SUGGESTION
     * ============================================================
     */

    private String calculateInvestmentSuggestion(
            FinancialSummary summary) {

        BigDecimal savingsRatio =
                summary.getSavingsRatio();

        if (savingsRatio.compareTo(
                new BigDecimal("40")) >= 0) {

            return "Consider increasing your monthly SIP investment.";
        }

        if (savingsRatio.compareTo(
                new BigDecimal("20")) >= 0) {

            return "Maintain your current investment strategy.";
        }

        return "Focus on building an emergency fund before investing.";
    }

    /*
     * ============================================================
     * FINANCIAL SUMMARY
     * ============================================================
     *
     * IMPORTANT:
     *
     * Only successful/completed transactions are included.
     *
     * Pending, failed, cancelled or invalid transactions
     * must not affect financial analysis.
     */

    private FinancialSummary calculateFinancialSummary(
            Wallet wallet,
            List<Transaction> transactions,
            String email) {

        BigDecimal totalIncome =
                BigDecimal.ZERO;

        BigDecimal totalExpense =
                BigDecimal.ZERO;

        int validTransactionCount = 0;

        for (Transaction transaction : transactions) {

            /*
             * Ignore invalid transaction records.
             */
            if (transaction == null
                    || transaction.getAmount() == null
                    || transaction.getStatus() == null) {

                continue;
            }

            /*
             * Only completed/successful transactions
             * affect financial analysis.
             */
            String status =
                    transaction.getStatus().name();

            boolean successful =
                    "COMPLETED".equalsIgnoreCase(status)
                    || "SUCCESS".equalsIgnoreCase(status)
                    || "SUCCESSFUL".equalsIgnoreCase(status);

            if (!successful) {
                continue;
            }

            String sender =
                    transaction.getSenderEmail();

            String receiver =
                    transaction.getReceiverEmail();

            /*
             * Ignore malformed transactions
             * where both directions are missing.
             */
            if (sender == null && receiver == null) {
                continue;
            }

            /*
             * Ignore self-transactions.
             *
             * Prevents the same transaction from being
             * counted as both income and expense.
             */
            if (email.equals(sender)
                    && email.equals(receiver)) {

                continue;
            }

            /*
             * Money received by the authenticated user
             * is income.
             */
            if (email.equals(receiver)) {

                totalIncome =
                        totalIncome.add(
                                transaction.getAmount()
                        );

                validTransactionCount++;

            }

            /*
             * Money sent by the authenticated user
             * is expense.
             */
            else if (email.equals(sender)) {

                totalExpense =
                        totalExpense.add(
                                transaction.getAmount()
                        );

                validTransactionCount++;
            }
        }

        /*
         * Savings = Income - Expense
         */
        BigDecimal savings =
                totalIncome.subtract(totalExpense);

        /*
         * Savings Ratio =
         *
         * (Savings / Income) * 100
         */
        BigDecimal savingsRatio =
                BigDecimal.ZERO;

        if (totalIncome.compareTo(
                BigDecimal.ZERO) > 0) {

            savingsRatio =
                    savings
                            .multiply(
                                    new BigDecimal("100")
                            )
                            .divide(
                                    totalIncome,
                                    2,
                                    RoundingMode.HALF_UP
                            );
        }

        return new FinancialSummary(
                wallet.getBalance(),
                totalIncome,
                totalExpense,
                savings,
                savingsRatio,
                validTransactionCount
        );
    }

    /*
     * ============================================================
     * FINANCIAL CONFIDENCE / SCORE
     * ============================================================
     */

    private int calculateConfidence(
            FinancialSummary summary) {

        int score = 50;

        if (summary.getSavingsRatio().compareTo(
                new BigDecimal("40")) >= 0) {

            score += 30;

        } else if (summary.getSavingsRatio().compareTo(
                new BigDecimal("20")) >= 0) {

            score += 20;

        } else if (summary.getSavingsRatio().compareTo(
                BigDecimal.ZERO) >= 0) {

            score += 10;
        }

        if (summary.getBalance().compareTo(
                new BigDecimal("100000")) >= 0) {

            score += 20;

        } else if (summary.getBalance().compareTo(
                new BigDecimal("50000")) >= 0) {

            score += 15;

        } else if (summary.getBalance().compareTo(
                new BigDecimal("10000")) >= 0) {

            score += 10;
        }

        return Math.min(score, 100);
    }

    /*
     * ============================================================
     * SAVING OPPORTUNITY
     * ============================================================
     */

    private String calculateSavingOpportunity(
            FinancialSummary summary) {

        BigDecimal savings =
                summary.getSavings();

        if (savings.compareTo(
                new BigDecimal("50000")) >= 0) {

            return "Excellent! You have a strong monthly saving capacity. "
                    + "Consider investing more in long-term wealth creation.";
        }

        if (savings.compareTo(
                new BigDecimal("20000")) >= 0) {

            return "Good savings. You can increase your SIP "
                    + "or build a larger emergency fund.";
        }

        if (savings.compareTo(
                BigDecimal.ZERO) > 0) {

            return "Try reducing unnecessary expenses "
                    + "to improve your monthly savings.";
        }

        return "Currently you are not saving money. "
                + "Focus on reducing expenses and increasing your income.";
    }

    /*
     * ============================================================
     * FINANCIAL RISK
     * ============================================================
     */

    private String calculateFinancialRisk(
            FinancialSummary summary) {

        if (summary.getSavings().compareTo(
                BigDecimal.ZERO) < 0) {

            return "High Risk: Your expenses are greater than your income.";
        }

        if (summary.getSavingsRatio().compareTo(
                new BigDecimal("10")) < 0) {

            return "Medium Risk: Your savings rate is low. "
                    + "Try reducing non-essential expenses.";
        }

        if (summary.getBalance().compareTo(
                new BigDecimal("5000")) < 0) {

            return "Medium Risk: Your wallet balance is low. "
                    + "Maintain an emergency fund.";
        }

        return "Low Risk: Your financial health is stable.";
    }

    /*
     * ============================================================
     * FINANCIAL SCORE
     * ============================================================
     */

    public FinancialScoreResponse getFinancialScore(
            String email) {

        logger.info(
                "Calculating financial score for user: {}",
                email
        );

        Wallet wallet =
                walletRepository.findByUserEmail(email);

        if (wallet == null) {

            logger.error(
                    "Wallet not found for user: {}",
                    email
            );

            throw new WalletNotFoundException(
                    "Wallet not found for email: " + email
            );
        }

        List<Transaction> transactions =
                transactionRepository.findBySenderEmailOrReceiverEmail(
                        email,
                        email
                );

        FinancialSummary summary =
                calculateFinancialSummary(
                        wallet,
                        transactions,
                        email
                );

        int score =
                calculateConfidence(summary);

        String rating;
        String risk;
        String remark;

        if (score >= 90) {

            rating = "Excellent";
            risk = "Low";
            remark = "Outstanding financial discipline.";

        } else if (score >= 75) {

            rating = "Good";
            risk = "Low";
            remark = "Your finances are healthy.";

        } else if (score >= 60) {

            rating = "Fair";
            risk = "Medium";
            remark = "There is room to improve your savings.";

        } else {

            rating = "Poor";
            risk = "High";
            remark = "Reduce expenses and increase savings.";
        }

        logger.info(
                "Financial score calculated successfully for user: {}",
                email
        );

        return new FinancialScoreResponse(
                score,
                rating,
                risk,
                remark
        );
    }

    /*
     * ============================================================
     * BUDGET HEALTH
     * ============================================================
     */

    public BudgetHealthResponse getBudgetHealth(
            String email) {

        logger.info(
                "Calculating budget health for user: {}",
                email
        );

        Wallet wallet =
                walletRepository.findByUserEmail(email);

        if (wallet == null) {

            logger.error(
                    "Wallet not found for user: {}",
                    email
            );

            throw new WalletNotFoundException(
                    "Wallet not found for email: " + email
            );
        }

        List<Transaction> transactions =
                transactionRepository.findBySenderEmailOrReceiverEmail(
                        email,
                        email
                );

        FinancialSummary summary =
                calculateFinancialSummary(
                        wallet,
                        transactions,
                        email
                );

        BigDecimal remainingBudget =
                summary.getSavings();

        int healthPercentage = 0;

        if (summary.getTotalIncome().compareTo(
                BigDecimal.ZERO) > 0) {

            healthPercentage =
                    remainingBudget
                            .multiply(
                                    new BigDecimal("100")
                            )
                            .divide(
                                    summary.getTotalIncome(),
                                    2,
                                    RoundingMode.HALF_UP
                            )
                            .intValue();
        }

        String status;

        if (healthPercentage >= 70) {

            status = "Excellent";

        } else if (healthPercentage >= 40) {

            status = "Healthy";

        } else if (healthPercentage >= 20) {

            status = "Average";

        } else {

            status = "Poor";
        }

        logger.info(
                "Budget health calculated successfully for user: {}",
                email
        );

        return new BudgetHealthResponse(
                summary.getTotalIncome(),
                summary.getTotalExpense(),
                remainingBudget,
                healthPercentage,
                status
        );
    }

    /*
     * ============================================================
     * GOAL RECOMMENDATION
     * ============================================================
     */

    public GoalRecommendationResponse getGoalRecommendation(
            String email) {

        logger.info(
                "Generating goal recommendation for user: {}",
                email
        );

        Wallet wallet =
                walletRepository.findByUserEmail(email);

        if (wallet == null) {

            logger.error(
                    "Wallet not found for user: {}",
                    email
            );

            throw new WalletNotFoundException(
                    "Wallet not found for email: " + email
            );
        }

        List<Transaction> transactions =
                transactionRepository.findBySenderEmailOrReceiverEmail(
                        email,
                        email
                );

        FinancialSummary summary =
                calculateFinancialSummary(
                        wallet,
                        transactions,
                        email
                );

        BigDecimal targetAmount =
                new BigDecimal("100000");

        BigDecimal currentAmount =
                wallet.getBalance();

        if (currentAmount.compareTo(
                BigDecimal.ZERO) < 0) {

            currentAmount = BigDecimal.ZERO;
        }

        int progressPercentage =
                currentAmount
                        .multiply(
                                new BigDecimal("100")
                        )
                        .divide(
                                targetAmount,
                                2,
                                RoundingMode.HALF_UP
                        )
                        .intValue();

        if (progressPercentage > 100) {

            progressPercentage = 100;
        }

        String recommendation;

        if (progressPercentage >= 100) {

            recommendation =
                    "Congratulations! You have achieved your financial goal.";

        } else if (progressPercentage >= 70) {

            recommendation =
                    "Great progress! Keep investing consistently.";

        } else if (progressPercentage >= 40) {

            recommendation =
                    "You are making steady progress. "
                    + "Increase monthly savings if possible.";

        } else {

            recommendation =
                    "Start building your savings with a monthly budget plan.";
        }

        logger.info(
                "Goal recommendation generated successfully for user: {}",
                email
        );

        return new GoalRecommendationResponse(
                "Emergency Fund",
                targetAmount,
                currentAmount,
                progressPercentage,
                recommendation
        );
    }

    /*
     * ============================================================
     * FINANCIAL CONTEXT
     * ============================================================
     */

    private FinancialContext buildFinancialContext(
            FinancialSummary summary) {

        return new FinancialContext(
                summary.getBalance(),
                summary.getTotalIncome(),
                summary.getTotalExpense(),
                summary.getSavings(),
                summary.getSavingsRatio(),
                summary.getTotalTransactions()
        );
    }

    /*
     * ============================================================
     * AI RESPONSE VALIDATION
     * ============================================================
     */

    private void validateAIInsightResponse(
            AIInsightResponse response) {

        if (response == null) {

            throw new AIServiceException(
                    "AI service returned a null response."
            );
        }

        if (response.getConfidence() < 0
                || response.getConfidence() > 100) {

            throw new AIServiceException(
                    "AI service returned an invalid confidence score."
            );
        }

        if (isBlank(response.getSavingOpportunity())) {

            throw new AIServiceException(
                    "AI service returned an empty saving opportunity."
            );
        }

        if (isBlank(response.getBudgetAlert())) {

            throw new AIServiceException(
                    "AI service returned an empty budget alert."
            );
        }

        if (isBlank(response.getInvestmentSuggestion())) {

            throw new AIServiceException(
                    "AI service returned an empty investment suggestion."
            );
        }

        if (isBlank(response.getFinancialRisk())) {

            throw new AIServiceException(
                    "AI service returned an empty financial risk."
            );
        }
    }

    private boolean isBlank(String value) {

        return value == null || value.isBlank();
    }
}