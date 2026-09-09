package com.taufiqhashmi.foundryai.workflows;

import com.taufiqhashmi.foundryai.agents.Agent;
import com.taufiqhashmi.foundryai.agents.AgentRegistry;
import com.taufiqhashmi.foundryai.agents.AgentResult;
import com.taufiqhashmi.foundryai.agents.AgentResultStatus;
import com.taufiqhashmi.foundryai.agents.AgentTask;
import com.taufiqhashmi.foundryai.agents.AgentType;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WorkflowEngineContextIntegrationTest {

    @Test
    void shouldPassDependencyResultToDependentAgent() {

        UUID financialTaskId =
                UUID.randomUUID();

        UUID engineeringTaskId =
                UUID.randomUUID();

        // -------------------------------------------------
        // Arrange: mock agents
        // -------------------------------------------------

        Agent cfoAgent =
                mock(Agent.class);

        Agent engineeringAgent =
                mock(Agent.class);

        when(cfoAgent.getType())
                .thenReturn(AgentType.CFO);

        when(engineeringAgent.getType())
                .thenReturn(AgentType.ENGINEERING);

        // -------------------------------------------------
        // Arrange: CFO result
        // -------------------------------------------------

        AgentResult financialResult =
                AgentResult.builder()
                        .agentType(AgentType.CFO)
                        .status(AgentResultStatus.SUCCESS)
                        .output(
                                "Financial analysis complete"
                        )
                        .build();

        when(cfoAgent.execute(
                any(AgentTask.class)
        )).thenReturn(financialResult);

        // -------------------------------------------------
        // Arrange: Engineering result
        // -------------------------------------------------

        AgentResult engineeringResult =
                AgentResult.builder()
                        .agentType(AgentType.ENGINEERING)
                        .status(AgentResultStatus.SUCCESS)
                        .output(
                                "Engineering analysis complete"
                        )
                        .build();

        when(engineeringAgent.execute(
                any(AgentTask.class)
        )).thenReturn(engineeringResult);

        // -------------------------------------------------
        // Arrange: workflow components
        // -------------------------------------------------

        AgentRegistry agentRegistry =
                new AgentRegistry(
                        List.of(
                                cfoAgent,
                                engineeringAgent
                        )
                );

        WorkflowTaskDispatcher dispatcher =
                new WorkflowTaskDispatcher(
                        agentRegistry
                );

        TaskDependencyResolver dependencyResolver =
                new TaskDependencyResolver();

        TaskContextBuilder contextBuilder =
                new TaskContextBuilder();

        WorkflowEngine workflowEngine =
                new WorkflowEngine(
                        dependencyResolver,
                        dispatcher,
                        contextBuilder
                );

        // -------------------------------------------------
        // Arrange: workflow tasks
        // -------------------------------------------------

        AgentTask financialTask =
                AgentTask.builder()
                        .taskId(financialTaskId)
                        .agentType(AgentType.CFO)
                        .objective(
                                "Perform financial analysis."
                        )
                        .dependencies(
                                List.of()
                        )
                        .build();

        AgentTask engineeringTask =
                AgentTask.builder()
                        .taskId(engineeringTaskId)
                        .agentType(
                                AgentType.ENGINEERING
                        )
                        .objective(
                                "Use the financial analysis."
                        )
                        .dependencies(
                                List.of(financialTaskId)
                        )
                        .build();

        // -------------------------------------------------
        // Arrange: execution plan
        // -------------------------------------------------

        ExecutionPlan plan =
                ExecutionPlan.builder()
                        .tasks(
                                List.of(
                                        financialTask,
                                        engineeringTask
                                )
                        )
                        .build();

        // -------------------------------------------------
        // Act
        // -------------------------------------------------

        WorkflowResult result =
                workflowEngine.execute(plan);

        // -------------------------------------------------
        // Assert: workflow completed successfully
        // -------------------------------------------------

        assertNotNull(result);

        assertTrue(
                result.isSuccessful()
        );

        assertEquals(
                WorkflowTaskStatus.SUCCESS,
                result.getTaskStatuses()
                        .get(financialTaskId)
        );

        assertEquals(
                WorkflowTaskStatus.SUCCESS,
                result.getTaskStatuses()
                        .get(engineeringTaskId)
        );

        // -------------------------------------------------
        // Assert: Engineering received dependency context
        // -------------------------------------------------

        ArgumentCaptor<AgentTask> taskCaptor =
                ArgumentCaptor.forClass(
                        AgentTask.class
                );

        verify(engineeringAgent)
                .execute(taskCaptor.capture());

        AgentTask executedEngineeringTask =
                taskCaptor.getValue();

        assertNotNull(
                executedEngineeringTask
        );

        assertNotNull(
                executedEngineeringTask.getContext()
        );

        assertNotNull(
                executedEngineeringTask
                        .getContext()
                        .getData()
        );

        Object dependencyResults =
                executedEngineeringTask
                        .getContext()
                        .getData()
                        .get("dependencyResults");

        assertNotNull(
                dependencyResults
        );

        @SuppressWarnings("unchecked")
        Map<UUID, AgentResult> dependencyResultMap =
                (Map<UUID, AgentResult>)
                        dependencyResults;

        AgentResult receivedFinancialResult =
                dependencyResultMap.get(
                        financialTaskId
                );

        assertNotNull(
                receivedFinancialResult
        );

        assertEquals(
                AgentType.CFO,
                receivedFinancialResult.getAgentType()
        );

        assertEquals(
                AgentResultStatus.SUCCESS,
                receivedFinancialResult.getStatus()
        );

        assertEquals(
                "Financial analysis complete",
                receivedFinancialResult.getOutput()
        );
    }
}