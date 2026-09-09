package com.taufiqhashmi.foundryai.workflows;

import com.taufiqhashmi.foundryai.agents.AgentTask;
import com.taufiqhashmi.foundryai.agents.AgentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExecutionPlanBuilderTest {

    private ExecutionPlanBuilder executionPlanBuilder;

    @BeforeEach
    void setUp() {
        executionPlanBuilder = new ExecutionPlanBuilder();
    }

    @Test
    void shouldGenerateRuntimeTaskIds() {

        PlannedTask firstTask = new PlannedTask(
                "financial_analysis",
                AgentType.CFO,
                "Analyze the financial impact.",
                List.of()
        );

        PlannedTask secondTask = new PlannedTask(
                "technical_analysis",
                AgentType.ENGINEERING,
                "Analyze the technical impact.",
                List.of()
        );

        ExecutionPlan plan =
                executionPlanBuilder.build(
                        List.of(firstTask, secondTask)
                );

        assertNotNull(plan);
        assertNotNull(plan.getTasks());

        assertEquals(2, plan.getTasks().size());

        AgentTask firstRuntimeTask =
                plan.getTasks().get(0);

        AgentTask secondRuntimeTask =
                plan.getTasks().get(1);

        assertNotNull(firstRuntimeTask.getTaskId());
        assertNotNull(secondRuntimeTask.getTaskId());

        assertTrue(
                !firstRuntimeTask.getTaskId()
                        .equals(secondRuntimeTask.getTaskId())
        );
    }

    @Test
    void shouldResolveDependencies() {

        PlannedTask financialTask = new PlannedTask(
                "financial_analysis",
                AgentType.CFO,
                "Analyze financial impact.",
                List.of()
        );

        PlannedTask engineeringTask = new PlannedTask(
                "technical_analysis",
                AgentType.ENGINEERING,
                "Analyze technical impact.",
                List.of("financial_analysis")
        );

        ExecutionPlan plan =
                executionPlanBuilder.build(
                        List.of(
                                financialTask,
                                engineeringTask
                        )
                );

        AgentTask runtimeFinancialTask =
                plan.getTasks().get(0);

        AgentTask runtimeEngineeringTask =
                plan.getTasks().get(1);

        assertTrue(
                runtimeEngineeringTask
                        .getDependencies()
                        .contains(
                                runtimeFinancialTask.getTaskId()
                        )
        );
    }

    @Test
    void shouldRejectUnknownDependencyKey() {

        PlannedTask task = new PlannedTask(
                "technical_analysis",
                AgentType.ENGINEERING,
                "Analyze technical impact.",
                List.of("unknown_task")
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> executionPlanBuilder.build(
                                List.of(task)
                        )
                );

        assertTrue(
                exception.getMessage()
                        .contains("unknown task key")
        );
    }

    @Test
    void shouldRejectDuplicateTaskKey() {

        PlannedTask firstTask = new PlannedTask(
                "financial_analysis",
                AgentType.CFO,
                "First financial analysis.",
                List.of()
        );

        PlannedTask secondTask = new PlannedTask(
                "financial_analysis",
                AgentType.CFO,
                "Second financial analysis.",
                List.of()
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> executionPlanBuilder.build(
                                List.of(
                                        firstTask,
                                        secondTask
                                )
                        )
                );

        assertTrue(
                exception.getMessage()
                        .contains("Duplicate task key")
        );
    }

    @Test
    void shouldRejectMissingTaskKey() {

        PlannedTask task = new PlannedTask(
                null,
                AgentType.CFO,
                "Analyze financial impact.",
                List.of()
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> executionPlanBuilder.build(
                                List.of(task)
                        )
                );

        assertTrue(
                exception.getMessage()
                        .contains("task key")
        );
    }
}