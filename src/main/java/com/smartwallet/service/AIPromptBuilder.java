package com.smartwallet.service;

import com.smartwallet.dto.FinancialContext;
import org.springframework.stereotype.Component;

@Component
public class AIPromptBuilder {

public String buildFinancialInsightPrompt(FinancialContext context) {

    return """
            You are the financial intelligence engine of NeuroWallet.

            Analyze the user's financial information provided below.

            Financial Context:
            - Balance: %s
            - Total Income: %s
            - Total Expense: %s
            - Savings: %s
            - Savings Ratio: %s%%
            - Total Transactions: %d

            Your task is to generate a financial insight.

            Return ONLY valid JSON.
            Do not use Markdown.
            Do not use ```json.
            Do not add any explanation outside the JSON.

            The JSON must follow exactly this structure:

            {
              "confidence": 0,
              "savingOpportunity": "",
              "budgetAlert": "",
              "investmentSuggestion": "",
              "financialRisk": ""
            }

            Rules:
            - confidence must be an integer from 0 to 100.
            - savingOpportunity must describe a practical saving opportunity.
            - budgetAlert must describe the user's budget condition.
            - investmentSuggestion must be responsible and based only on the provided data.
            - financialRisk must describe the user's financial risk.
            - Do not invent transactions, income, expenses, or balances.
            - Do not execute or suggest executing financial transactions automatically.
            - Base the response only on the supplied financial context.
            """.formatted(
            context.getBalance(),
            context.getTotalIncome(),
            context.getTotalExpense(),
            context.getSavings(),
            context.getSavingsRatio(),
            context.getTotalTransactions()
    );
}
}