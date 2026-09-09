package com.taufiqhashmi.foundryai.agents.implementations;

import com.taufiqhashmi.foundryai.agents.AgentResult;
import com.taufiqhashmi.foundryai.agents.AgentTask;
import com.taufiqhashmi.foundryai.agents.AgentType;
import com.taufiqhashmi.foundryai.tools.implementations.FinancialCalculatorTool;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
class CEOAgentIntegrationTest {

    private final CEOAgent ceoAgent;

    @MockitoSpyBean 
    private final FinancialCalculatorTool financialCalculatorTool;

    @Autowired
    CEOAgentIntegrationTest(
            CEOAgent ceoAgent,
            FinancialCalculatorTool financialCalculatorTool
    ) {
        this.ceoAgent = ceoAgent;
        this.financialCalculatorTool = financialCalculatorTool;
    }

    @Test
    void shouldExecuteCeoAgentAgainstRealModel() {

        AgentTask task = AgentTask.builder()
                .objective(
                        "Analyze this business objective and provide a concise strategic recommendation: "
                                + "Should a fintech startup prioritize reducing cloud infrastructure costs "
                                + "or accelerating product development over the next quarter?"
                )
                .build();

        AgentResult result = ceoAgent.execute(task);

        assertNotNull(result);

        printResult("CEO AGENT EXECUTION", result);

        assertEquals(
                AgentType.CEO,
                result.getAgentType()
        );

        assertTrue(
                result.isSuccessful(),
                "CEO agent execution failed: " + result.getError()
        );

        assertNotNull(result.getOutput());

        assertFalse(
                result.getOutput().isBlank()
        );
    }

    @Test
    void shouldUseFinancialCalculatorTool() {

        AgentTask task = AgentTask.builder()
                .objective(
                        "Use the financial-calculator tool to calculate the total cost "
                                + "of a cloud service that costs $1250.50 per month for 12 months. "
                                + "Return the calculated total and briefly explain the result."
                )
                .build();

        AgentResult result = ceoAgent.execute(task);

        assertNotNull(result);

        printResult("CEO AGENT TOOL EXECUTION", result);

        assertEquals(
                AgentType.CEO,
                result.getAgentType()
        );

        assertTrue(
                result.isSuccessful(),
                "CEO agent execution failed: " + result.getError()
        );

        assertNotNull(result.getOutput());

        assertFalse(
                result.getOutput().isBlank()
        );

        assertTrue(
                result.getOutput().contains("15,006")
                        || result.getOutput().contains("15006"),
                "Expected calculated total 15,006 in output, but got: "
                        + result.getOutput()
        );

        ArgumentCaptor<BigDecimal> monthlyCostCaptor =
                ArgumentCaptor.forClass(BigDecimal.class);

        ArgumentCaptor<Integer> monthsCaptor =
                ArgumentCaptor.forClass(Integer.class);

        verify(financialCalculatorTool, atLeastOnce())
                .calculateAnnualCost(
                        monthlyCostCaptor.capture(),
                        monthsCaptor.capture()
                );

        assertEquals(
                new BigDecimal("1250.5"),
                monthlyCostCaptor.getValue()
        );

        assertEquals(
                12,
                monthsCaptor.getValue()
        );
    }

    private void printResult(
            String title,
            AgentResult result
    ) {
        System.out.println("========================================");
        System.out.println(title);
        System.out.println("========================================");
        System.out.println("Agent Type : " + result.getAgentType());
        System.out.println("Status     : " + result.getStatus());
        System.out.println("Successful : " + result.isSuccessful());
        System.out.println("Output     : " + result.getOutput());
        System.out.println("Error      : " + result.getError());
        System.out.println("Metadata   : " + result.getMetadata());
        System.out.println("========================================");
    }
}