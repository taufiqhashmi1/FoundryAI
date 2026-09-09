package com.taufiqhashmi.foundryai.tools.implementations;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FinancialCalculatorTool {

        @Tool(name = "financial-calculator", description = """
                        Performs deterministic financial arithmetic using the
                        exact numeric inputs supplied by the caller.

                        Use this tool whenever calculating financial totals,
                        savings, costs, net impact, margins, ROI, or similar
                        numerical results.

                        Do not invent input values. Only calculate using
                        values provided in the task or obtained from an
                        actual tool result.
                        """)
        public BigDecimal calculateAnnualCost(
                        BigDecimal monthlyCost,
                        int months) {
                if (monthlyCost == null) {
                        throw new IllegalArgumentException(
                                        "Monthly cost cannot be null");
                }

                if (monthlyCost.signum() < 0) {
                        throw new IllegalArgumentException(
                                        "Monthly cost cannot be negative");
                }

                if (months <= 0) {
                        throw new IllegalArgumentException(
                                        "Number of months must be greater than zero");
                }

                return monthlyCost.multiply(
                                BigDecimal.valueOf(months));
        }
}