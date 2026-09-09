package com.taufiqhashmi.foundryai.tools.implementations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FinancialCalculatorToolTest {

    private FinancialCalculatorTool financialCalculatorTool;

    @BeforeEach
    void setUp() {
        financialCalculatorTool = new FinancialCalculatorTool();
    }

    @Test
    void shouldCalculateCostForGivenNumberOfMonths() {

        BigDecimal monthlyCost = new BigDecimal("1250.50");

        BigDecimal result =
                financialCalculatorTool.calculateAnnualCost(
                        monthlyCost,
                        12
                );

        assertEquals(
                new BigDecimal("15006.00"),
                result
        );
    }

    @Test
    void shouldCalculateCostForSingleMonth() {

        BigDecimal monthlyCost = new BigDecimal("1000.00");

        BigDecimal result =
                financialCalculatorTool.calculateAnnualCost(
                        monthlyCost,
                        1
                );

        assertEquals(
                new BigDecimal("1000.00"),
                result
        );
    }

    @Test
    void shouldRejectNullMonthlyCost() {

        assertThrows(
                IllegalArgumentException.class,
                () -> financialCalculatorTool.calculateAnnualCost(
                        null,
                        12
                )
        );
    }

    @Test
    void shouldRejectNegativeMonthlyCost() {

        BigDecimal monthlyCost = new BigDecimal("-100.00");

        assertThrows(
                IllegalArgumentException.class,
                () -> financialCalculatorTool.calculateAnnualCost(
                        monthlyCost,
                        12
                )
        );
    }

    @Test
    void shouldRejectZeroMonths() {

        BigDecimal monthlyCost = new BigDecimal("1000.00");

        assertThrows(
                IllegalArgumentException.class,
                () -> financialCalculatorTool.calculateAnnualCost(
                        monthlyCost,
                        0
                )
        );
    }

    @Test
    void shouldRejectNegativeMonths() {

        BigDecimal monthlyCost = new BigDecimal("1000.00");

        assertThrows(
                IllegalArgumentException.class,
                () -> financialCalculatorTool.calculateAnnualCost(
                        monthlyCost,
                        -1
                )
        );
    }
}