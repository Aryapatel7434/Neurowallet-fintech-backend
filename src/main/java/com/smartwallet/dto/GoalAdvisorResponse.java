
package com.smartwallet.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GoalAdvisorResponse {

    private LocalDateTime generatedAt;

    // Financial overview based on recorded transactions
    private BigDecimal totalRecordedIncome;
    private BigDecimal totalRecordedExpense;
    private BigDecimal netRecordedCashFlow;

    // Goal overview
    private int activeGoals;
    private int goalsAtRisk;

    private List<GoalPlan> goalPlans = new ArrayList<>();

    // Gemini-generated analysis
    private String overallAssessment;

    private List<String> priorityActions = new ArrayList<>();

    private String dataLimitations;

    private String disclaimer =
            "AI-generated information is educational and "
            + "does not guarantee financial outcomes.";

    public GoalAdvisorResponse() {
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public BigDecimal getTotalRecordedIncome() {
        return totalRecordedIncome;
    }

    public void setTotalRecordedIncome(
            BigDecimal totalRecordedIncome) {
        this.totalRecordedIncome = totalRecordedIncome;
    }

    public BigDecimal getTotalRecordedExpense() {
        return totalRecordedExpense;
    }

    public void setTotalRecordedExpense(
            BigDecimal totalRecordedExpense) {
        this.totalRecordedExpense = totalRecordedExpense;
    }

    public BigDecimal getNetRecordedCashFlow() {
        return netRecordedCashFlow;
    }

    public void setNetRecordedCashFlow(
            BigDecimal netRecordedCashFlow) {
        this.netRecordedCashFlow = netRecordedCashFlow;
    }

    public int getActiveGoals() {
        return activeGoals;
    }

    public void setActiveGoals(int activeGoals) {
        this.activeGoals = activeGoals;
    }

    public int getGoalsAtRisk() {
        return goalsAtRisk;
    }

    public void setGoalsAtRisk(int goalsAtRisk) {
        this.goalsAtRisk = goalsAtRisk;
    }

    public List<GoalPlan> getGoalPlans() {
        return goalPlans;
    }

    public void setGoalPlans(List<GoalPlan> goalPlans) {
        this.goalPlans = goalPlans;
    }

    public String getOverallAssessment() {
        return overallAssessment;
    }

    public void setOverallAssessment(
            String overallAssessment) {
        this.overallAssessment = overallAssessment;
    }

    public List<String> getPriorityActions() {
        return priorityActions;
    }

    public void setPriorityActions(
            List<String> priorityActions) {
        this.priorityActions = priorityActions;
    }

    public String getDataLimitations() {
        return dataLimitations;
    }

    public void setDataLimitations(String dataLimitations) {
        this.dataLimitations = dataLimitations;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }

    // =====================================================
    // INDIVIDUAL GOAL ANALYSIS
    // =====================================================

    public static class GoalPlan {

        private Long goalId;

        private String goalName;

        private String status;

        private BigDecimal targetAmount;

        private BigDecimal currentAmount;

        private BigDecimal remainingAmount;

        private BigDecimal progressPercentage;

        private LocalDate targetDate;

        private long remainingDays;

        private BigDecimal requiredMonthlyContribution;

        private String feasibilityStatus;

        private String advisorRecommendation;

        public GoalPlan() {
        }

        public Long getGoalId() {
            return goalId;
        }

        public void setGoalId(Long goalId) {
            this.goalId = goalId;
        }

        public String getGoalName() {
            return goalName;
        }

        public void setGoalName(String goalName) {
            this.goalName = goalName;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public BigDecimal getTargetAmount() {
            return targetAmount;
        }

        public void setTargetAmount(BigDecimal targetAmount) {
            this.targetAmount = targetAmount;
        }

        public BigDecimal getCurrentAmount() {
            return currentAmount;
        }

        public void setCurrentAmount(BigDecimal currentAmount) {
            this.currentAmount = currentAmount;
        }

        public BigDecimal getRemainingAmount() {
            return remainingAmount;
        }

        public void setRemainingAmount(
                BigDecimal remainingAmount) {
            this.remainingAmount = remainingAmount;
        }

        public BigDecimal getProgressPercentage() {
            return progressPercentage;
        }

        public void setProgressPercentage(
                BigDecimal progressPercentage) {
            this.progressPercentage = progressPercentage;
        }

        public LocalDate getTargetDate() {
            return targetDate;
        }

        public void setTargetDate(LocalDate targetDate) {
            this.targetDate = targetDate;
        }

        public long getRemainingDays() {
            return remainingDays;
        }

        public void setRemainingDays(long remainingDays) {
            this.remainingDays = remainingDays;
        }

        public BigDecimal getRequiredMonthlyContribution() {
            return requiredMonthlyContribution;
        }

        public void setRequiredMonthlyContribution(
                BigDecimal requiredMonthlyContribution) {
            this.requiredMonthlyContribution =
                    requiredMonthlyContribution;
        }

        public String getFeasibilityStatus() {
            return feasibilityStatus;
        }

        public void setFeasibilityStatus(
                String feasibilityStatus) {
            this.feasibilityStatus = feasibilityStatus;
        }

        public String getAdvisorRecommendation() {
            return advisorRecommendation;
        }

        public void setAdvisorRecommendation(
                String advisorRecommendation) {
            this.advisorRecommendation = advisorRecommendation;
        }
    }
}
