package com.taufiqhashmi.foundryai.agents;

import com.taufiqhashmi.foundryai.ai.AiModelGateway;
import com.taufiqhashmi.foundryai.workflows.ExecutionPlan;
import com.taufiqhashmi.foundryai.workflows.ExecutionPlanBuilder;
import com.taufiqhashmi.foundryai.workflows.PlannedTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CEOPlannerTest {

    @Mock
    private AiModelGateway aiModelGateway;

    @Mock
    private ExecutionPlanBuilder executionPlanBuilder;

    private CEOPlanner ceoPlanner;

    @BeforeEach
    void setUp() {

        ceoPlanner = new CEOPlanner(
                aiModelGateway,
                executionPlanBuilder
        );
    }

    @Test
    void shouldCreateExecutionPlan() {

        String businessObjective =
                "Determine whether the fintech startup "
                        + "should reduce cloud costs or accelerate "
                        + "product development.";

        List<PlannedTask> plannedTasks =
                List.of(
                        new PlannedTask(
                                "financial_analysis",
                                AgentType.CFO,
                                "Analyze financial impact.",
                                List.of()
                        ),
                        new PlannedTask(
                                "technical_analysis",
                                AgentType.ENGINEERING,
                                "Analyze product development impact.",
                                List.of()
                        )
                );

        ExecutionPlan expectedPlan =
                ExecutionPlan.builder()
                        .tasks(List.of())
                        .build();

        when(aiModelGateway.generatePlan(
                AgentType.CEO,
                businessObjective
        )).thenReturn(plannedTasks);

        when(executionPlanBuilder.build(
                plannedTasks
        )).thenReturn(expectedPlan);

        ExecutionPlan actualPlan =
                ceoPlanner.createPlan(
                        businessObjective
                );

        assertNotNull(actualPlan);

        assertEquals(
                expectedPlan,
                actualPlan
        );

        verify(aiModelGateway).generatePlan(
                AgentType.CEO,
                businessObjective
        );

        verify(executionPlanBuilder).build(
                plannedTasks
        );
    }

    @Test
    void shouldRejectNullBusinessObjective() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> ceoPlanner.createPlan(null)
                );

        assertEquals(
                "Business objective cannot be null or blank",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectBlankBusinessObjective() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> ceoPlanner.createPlan("   ")
                );

        assertEquals(
                "Business objective cannot be null or blank",
                exception.getMessage()
        );
    }
}