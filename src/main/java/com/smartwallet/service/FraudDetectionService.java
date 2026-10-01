package com.smartwallet.service;

import com.smartwallet.dto.FraudAIAnalysisResponse;
import com.smartwallet.dto.FraudDetectionResponse;
import com.smartwallet.model.FraudRiskLevel;
import com.smartwallet.model.Transaction;
import com.smartwallet.model.TransactionStatus;
import com.smartwallet.repository.TransactionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service
public class FraudDetectionService {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    FraudDetectionService.class
            );

    private static final BigDecimal HIGH_AMOUNT =
            new BigDecimal("50000");

    private static final BigDecimal VERY_HIGH_AMOUNT =
            new BigDecimal("100000");

    private static final BigDecimal AMOUNT_RATIO_THRESHOLD =
            new BigDecimal("3");

    private static final int RAPID_TRANSACTION_LIMIT = 3;

    private static final int FAILED_TRANSACTION_LIMIT = 3;

    private final TransactionRepository transactionRepository;

    private final FraudAIAnalysisService fraudAIAnalysisService;

    public FraudDetectionService(
            TransactionRepository transactionRepository,
            FraudAIAnalysisService fraudAIAnalysisService) {

        this.transactionRepository =
                transactionRepository;

        this.fraudAIAnalysisService =
                fraudAIAnalysisService;
    }

    public FraudDetectionResponse detectFraud(
            Long transactionId,
            String email) {

        Transaction transaction =
                transactionRepository
                        .findById(transactionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Transaction not found"
                                )
                        );

        validateOwnership(
                transaction,
                email
        );

        /*
         * ============================================================
         * LOAD ONLY THE HISTORY REQUIRED BY EACH FRAUD RULE
         * ============================================================
         */

        List<Transaction> successfulHistory =
                transactionRepository
                        .findUserTransactionsByStatus(
                                email,
                                TransactionStatus.SUCCESS
                        );

        List<Transaction> recentHistory =
                loadRecentHistory(
                        transaction,
                        email
                );

        List<Transaction> recentFailedHistory =
                loadRecentFailedHistory(
                        transaction,
                        email
                );

        /*
         * ============================================================
         * RULE-BASED FRAUD ANALYSIS
         * ============================================================
         */

        BigDecimal riskScore =
                calculateRiskScore(
                        transaction,
                        successfulHistory,
                        recentHistory,
                        recentFailedHistory
                );

        FraudRiskLevel riskLevel =
                determineRiskLevel(
                        riskScore
                );

        List<String> riskFactors =
                identifyRiskFactors(
                        transaction,
                        successfulHistory,
                        recentHistory,
                        recentFailedHistory
                );

        String reason =
                buildReason(
                        riskLevel,
                        riskFactors
                );

        String recommendation =
                buildRecommendation(
                        riskLevel
                );

        /*
         * ============================================================
         * AI FRAUD ANALYSIS
         *
         * AI is an enhancement layer.
         *
         * The deterministic rule engine remains the
         * source of truth for risk score and risk level.
         * ============================================================
         */

        FraudAIAnalysisResponse aiAnalysis = null;

        try {

            aiAnalysis =
                    fraudAIAnalysisService.analyze(
                            riskLevel.name(),
                            riskScore.toPlainString(),
                            reason,
                            String.join(
                                    ", ",
                                    riskFactors
                            )
                    );

        } catch (Exception ex) {

            /*
             * Gemini failure must not make the
             * fraud detection engine unavailable.
             *
             * The rule-based fraud result remains
             * valid and will still be returned.
             */

            logger.warn(
                    "AI fraud analysis unavailable. "
                    + "Returning rule-based fraud result."
            );
        }

        /*
         * ============================================================
         * FINAL RESPONSE
         * ============================================================
         */

        FraudDetectionResponse response =
                new FraudDetectionResponse(
                        transactionId,
                        riskLevel,
                        riskScore,
                        reason,
                        recommendation,
                        riskFactors
                );

        response.setAiAnalysis(
                aiAnalysis
        );

        return response;
    }

    private List<Transaction> loadRecentHistory(
            Transaction transaction,
            String email) {

        if (transaction.getTimestamp() == null) {

            return new ArrayList<>();
        }

        LocalDateTime from =
                transaction.getTimestamp()
                        .minusMinutes(10);

        return transactionRepository
                .findUserTransactionsSince(
                        email,
                        from
                );
    }

    private List<Transaction> loadRecentFailedHistory(
            Transaction transaction,
            String email) {

        if (transaction.getTimestamp() == null) {

            return new ArrayList<>();
        }

        LocalDateTime from =
                transaction.getTimestamp()
                        .minusMinutes(60);

        return transactionRepository
                .findRecentFailedTransactions(
                        email,
                        TransactionStatus.FAILED,
                        TransactionStatus.FAILD,
                        from
                );
    }

    private void validateOwnership(
            Transaction transaction,
            String email) {

        boolean sender =
                email.equals(
                        transaction.getSenderEmail()
                );

        boolean receiver =
                email.equals(
                        transaction.getReceiverEmail()
                );

        if (!sender && !receiver) {

            throw new RuntimeException(
                    "Transaction access denied"
            );
        }
    }

    private BigDecimal calculateRiskScore(
            Transaction transaction,
            List<Transaction> successfulHistory,
            List<Transaction> recentHistory,
            List<Transaction> recentFailedHistory) {

        BigDecimal score =
                BigDecimal.ZERO;

        if (isHighAmount(transaction)) {

            score = score.add(
                    new BigDecimal("20")
            );
        }

        if (isVeryHighAmount(transaction)) {

            score = score.add(
                    new BigDecimal("15")
            );
        }

        if (isAmountAnomaly(
                transaction,
                successfulHistory)) {

            score = score.add(
                    new BigDecimal("25")
            );
        }

        if (hasRapidTransactions(
                transaction,
                recentHistory)) {

            score = score.add(
                    new BigDecimal("20")
            );
        }

        if (hasRecentFailedTransactions(
                transaction,
                recentFailedHistory)) {

            score = score.add(
                    new BigDecimal("15")
            );
        }

        if (isUnusualCategory(
                transaction,
                successfulHistory)) {

            score = score.add(
                    new BigDecimal("10")
            );
        }

        if (score.compareTo(
                new BigDecimal("100")
        ) > 0) {

            score = new BigDecimal("100");
        }

        return score.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }

    private List<String> identifyRiskFactors(
            Transaction transaction,
            List<Transaction> successfulHistory,
            List<Transaction> recentHistory,
            List<Transaction> recentFailedHistory) {

        List<String> factors =
                new ArrayList<>();

        if (isHighAmount(transaction)) {

            factors.add(
                    "High transaction amount"
            );
        }

        if (isVeryHighAmount(transaction)) {

            factors.add(
                    "Very high transaction amount"
            );
        }

        if (isAmountAnomaly(
                transaction,
                successfulHistory)) {

            factors.add(
                    "Transaction amount is significantly higher than the user's historical average"
            );
        }

        if (hasRapidTransactions(
                transaction,
                recentHistory)) {

            factors.add(
                    "Multiple transactions detected within a short time window"
            );
        }

        if (hasRecentFailedTransactions(
                transaction,
                recentFailedHistory)) {

            factors.add(
                    "Multiple recent failed transactions detected"
            );
        }

        if (isUnusualCategory(
                transaction,
                successfulHistory)) {

            factors.add(
                    "Transaction category is unusual compared with historical spending"
            );
        }

        if (factors.isEmpty()) {

            factors.add(
                    "No significant rule-based risk factor detected"
            );
        }

        return factors;
    }

    private boolean isHighAmount(
            Transaction transaction) {

        return transaction.getAmount() != null
                && transaction.getAmount()
                        .abs()
                        .compareTo(HIGH_AMOUNT) > 0;
    }

    private boolean isVeryHighAmount(
            Transaction transaction) {

        return transaction.getAmount() != null
                && transaction.getAmount()
                        .abs()
                        .compareTo(VERY_HIGH_AMOUNT) > 0;
    }

    private boolean isAmountAnomaly(
            Transaction transaction,
            List<Transaction> history) {

        if (transaction.getAmount() == null
                || history == null
                || history.isEmpty()) {

            return false;
        }

        BigDecimal total =
                BigDecimal.ZERO;

        int count = 0;

        for (Transaction historical :
                history) {

            if (historical == null
                    || historical.getAmount() == null
                    || historical.getStatus()
                    != TransactionStatus.SUCCESS) {

                continue;
            }

            if (historical.getTransactionId()
                    != null
                    && historical.getTransactionId()
                    .equals(
                            transaction.getTransactionId()
                    )) {

                continue;
            }

            total = total.add(
                    historical.getAmount().abs()
            );

            count++;
        }

        if (count == 0) {

            return false;
        }

        BigDecimal average =
                total.divide(
                        BigDecimal.valueOf(count),
                        2,
                        RoundingMode.HALF_UP
                );

        if (average.compareTo(
                BigDecimal.ZERO
        ) <= 0) {

            return false;
        }

        BigDecimal currentAmount =
                transaction.getAmount().abs();

        return currentAmount.compareTo(
                average.multiply(
                        AMOUNT_RATIO_THRESHOLD
                )
        ) > 0;
    }

    private boolean hasRapidTransactions(
            Transaction transaction,
            List<Transaction> recentHistory) {

        if (transaction.getTimestamp() == null
                || recentHistory == null) {

            return false;
        }

        int count = 0;

        for (Transaction historical :
                recentHistory) {

            if (historical == null
                    || historical.getTimestamp() == null
                    || historical.getTransactionId()
                    == null) {

                continue;
            }

            if (historical.getTransactionId()
                    .equals(
                            transaction.getTransactionId()
                    )) {

                continue;
            }

            if (historical.getStatus()
                    != TransactionStatus.SUCCESS) {

                continue;
            }

            count++;
        }

        return count >= RAPID_TRANSACTION_LIMIT;
    }

    private boolean hasRecentFailedTransactions(
            Transaction transaction,
            List<Transaction> recentFailedHistory) {

        if (transaction.getTimestamp() == null
                || recentFailedHistory == null) {

            return false;
        }

        int count = 0;

        for (Transaction historical :
                recentFailedHistory) {

            if (historical == null
                    || historical.getTransactionId()
                    == null) {

                continue;
            }

            if (historical.getTransactionId()
                    .equals(
                            transaction.getTransactionId()
                    )) {

                continue;
            }

            count++;
        }

        return count >= FAILED_TRANSACTION_LIMIT;
    }

    private boolean isUnusualCategory(
            Transaction transaction,
            List<Transaction> history) {

        if (transaction.getCategory() == null
                || history == null
                || history.isEmpty()) {

            return false;
        }

        long categoryCount = 0;

        long successfulCount = 0;

        for (Transaction historical :
                history) {

            if (historical == null
                    || historical.getStatus()
                    != TransactionStatus.SUCCESS
                    || historical.getCategory()
                    == null) {

                continue;
            }

            if (historical.getTransactionId()
                    != null
                    && historical.getTransactionId()
                    .equals(
                            transaction.getTransactionId()
                    )) {

                continue;
            }

            successfulCount++;

            if (historical.getCategory()
                    == transaction.getCategory()) {

                categoryCount++;
            }
        }

        if (successfulCount < 5) {

            return false;
        }

        BigDecimal categoryRatio =
                BigDecimal.valueOf(categoryCount)
                        .divide(
                                BigDecimal.valueOf(
                                        successfulCount
                                ),
                                4,
                                RoundingMode.HALF_UP
                        );

        return categoryRatio.compareTo(
                new BigDecimal("0.05")
        ) < 0;
    }

    private FraudRiskLevel determineRiskLevel(
            BigDecimal score) {

        if (score.compareTo(
                new BigDecimal("85")
        ) >= 0) {

            return FraudRiskLevel.CRITICAL;
        }

        if (score.compareTo(
                new BigDecimal("60")
        ) >= 0) {

            return FraudRiskLevel.HIGH;
        }

        if (score.compareTo(
                new BigDecimal("30")
        ) >= 0) {

            return FraudRiskLevel.MEDIUM;
        }

        return FraudRiskLevel.LOW;
    }

    private String buildReason(
            FraudRiskLevel riskLevel,
            List<String> riskFactors) {

        return "Transaction classified as "
                + riskLevel
                + " based on detected risk factors: "
                + String.join(
                        ", ",
                        riskFactors
                );
    }

    private String buildRecommendation(
            FraudRiskLevel riskLevel) {

        switch (riskLevel) {

            case CRITICAL:
                return "Review the transaction immediately.";

            case HIGH:
                return "Perform additional verification.";

            case MEDIUM:
                return "Monitor the transaction.";

            default:
                return "No immediate fraud action required.";
        }
    }
}