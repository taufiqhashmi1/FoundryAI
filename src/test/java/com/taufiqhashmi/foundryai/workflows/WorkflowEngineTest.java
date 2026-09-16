package com.taufiqhashmi.foundryai.workflows;

import com.taufiqhashmi.foundryai.agents.AgentResult;
import com.taufiqhashmi.foundryai.agents.AgentResultStatus;
import com.taufiqhashmi.foundryai.agents.AgentTask;
import com.taufiqhashmi.foundryai.agents.AgentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WorkflowEngineTest {

    private TaskDependencyResolver dependencyResolver;
    private WorkflowTaskDispatcher taskDispatcher;
    private WorkflowEngine workflowEngine;
    private TaskContextBuilder taskContextBuilder;

    @BeforeEach
    void setUp() {

        dependencyResolver = new TaskDependencyResolver();

        taskDispatcher = mock(WorkflowTaskDispatcher.class);

        taskContextBuilder = new TaskContextBuilder();

        workflowEngine = new WorkflowEngine(
                dependencyResolver,
                taskDispatcher,
                taskContextBuilder
        );
    }

    @Test
    void shouldExecuteIndependentTasks() {

        AgentTask cfoTask = AgentTask.builder()
                .taskId(UUID.randomUUID())
                .agentType(AgentType.CFO)
                .objective("Analyze financial position")
                .build();

        AgentTask engineeringTask = AgentTask.builder()
                .taskId(UUID.randomUUID())
                .agentType(AgentType.ENGINEERING)
                .objective("Analyze technical requirements")
                .build();

        AgentResult cfoResult =
                successfulResult(AgentType.CFO);

        AgentResult engineeringResult =
                successfulResult(AgentType.ENGINEERING);

        when(taskDispatcher.dispatch(cfoTask))
                .thenReturn(cfoResult);

        when(taskDispatcher.dispatch(engineeringTask))
                .thenReturn(engineeringResult);

        ExecutionPlan plan = ExecutionPlan.builder()
                .tasks(
                        List.of(
                                cfoTask,
                                engineeringTask
                        )
                )
                .build();

        WorkflowResult result =
                workflowEngine.execute(plan);

        assertTrue(
                result.isSuccessful()
        );

        assertEquals(
                2,
                result.getResults().size()
        );

        assertTrue(
                result.getFailedTasks().isEmpty()
        );

        assertEquals(
                cfoResult,
                result.getResults()
                        .get(cfoTask.getTaskId())
        );

        assertEquals(
                engineeringResult,
                result.getResults()
                        .get(engineeringTask.getTaskId())
        );

        verify(taskDispatcher)
                .dispatch(cfoTask);

        verify(taskDispatcher)
                .dispatch(engineeringTask);
    }

    @Test
    void shouldRespectTaskDependencies() {

        UUID cfoTaskId = UUID.randomUUID();

        AgentTask cfoTask = AgentTask.builder()
                .taskId(cfoTaskId)
                .agentType(AgentType.CFO)
                .objective("Calculate financial impact")
                .build();

        AgentTask engineeringTask = AgentTask.builder()
                .taskId(UUID.randomUUID())
                .agentType(AgentType.ENGINEERING)
                .objective("Evaluate engineering impact")
                .dependencies(
                        List.of(cfoTaskId)
                )
                .build();

        AgentResult cfoResult =
                successfulResult(AgentType.CFO);

        AgentResult engineeringResult =
                successfulResult(AgentType.ENGINEERING);

        when(taskDispatcher.dispatch(cfoTask))
                .thenReturn(cfoResult);

        when(taskDispatcher.dispatch(engineeringTask))
                .thenReturn(engineeringResult);

        ExecutionPlan plan = ExecutionPlan.builder()
                .tasks(
                        List.of(
                                engineeringTask,
                                cfoTask
                        )
                )
                .build();

        WorkflowResult result =
                workflowEngine.execute(plan);

        assertTrue(
                result.isSuccessful()
        );

        verify(taskDispatcher)
                .dispatch(cfoTask);

        verify(taskDispatcher)
                .dispatch(engineeringTask);
    }

    @Test
    void shouldCollectAgentResults() {

        AgentTask task = AgentTask.builder()
                .taskId(UUID.randomUUID())
                .agentType(AgentType.CFO)
                .objective("Analyze costs")
                .build();

        AgentResult agentResult =
                AgentResult.builder()
                        .agentType(AgentType.CFO)
                        .status(AgentResultStatus.SUCCESS)
                        .output("Cost analysis completed")
                        .build();

        when(taskDispatcher.dispatch(task))
                .thenReturn(agentResult);

        ExecutionPlan plan = ExecutionPlan.builder()
                .tasks(List.of(task))
                .build();

        WorkflowResult workflowResult =
                workflowEngine.execute(plan);

        assertTrue(
                workflowResult.isSuccessful()
        );

        AgentResult actualResult =
                workflowResult.getResults()
                        .get(task.getTaskId());

        assertNotNull(actualResult);

        assertEquals(
                AgentType.CFO,
                actualResult.getAgentType()
        );

        assertEquals(
                AgentResultStatus.SUCCESS,
                actualResult.getStatus()
        );

        assertEquals(
                "Cost analysis completed",
                actualResult.getOutput()
        );
    }

    @Test
    void shouldRecordFailedTasks() {

        AgentTask task = AgentTask.builder()
                .taskId(UUID.randomUUID())
                .agentType(AgentType.CFO)
                .objective("Analyze invalid financial data")
                .build();

        AgentResult failureResult =
                AgentResult.builder()
                        .agentType(AgentType.CFO)
                        .status(AgentResultStatus.FAILURE)
                        .error("Financial data is invalid")
                        .build();

        when(taskDispatcher.dispatch(task))
                .thenReturn(failureResult);

        ExecutionPlan plan = ExecutionPlan.builder()
                .tasks(List.of(task))
                .build();

        WorkflowResult result =
                workflowEngine.execute(plan);

        assertFalse(
                result.isSuccessful()
        );

        assertTrue(
                result.getFailedTasks()
                        .contains(task.getTaskId())
        );

        AgentResult actualResult =
                result.getResults()
                        .get(task.getTaskId());

        assertNotNull(actualResult);

        assertEquals(
                AgentType.CFO,
                actualResult.getAgentType()
        );

        assertEquals(
                AgentResultStatus.FAILURE,
                actualResult.getStatus()
        );

        assertEquals(
                "Financial data is invalid",
                actualResult.getError()
        );
    }

    @Test
    void shouldConvertDispatcherExceptionIntoFailedResult() {

        AgentTask task = AgentTask.builder()
                .taskId(UUID.randomUUID())
                .agentType(AgentType.CFO)
                .objective("Execute failing task")
                .build();

        when(taskDispatcher.dispatch(task))
                .thenThrow(
                        new RuntimeException(
                                "Agent execution failed"
                        )
                );

        ExecutionPlan plan = ExecutionPlan.builder()
                .tasks(List.of(task))
                .build();

        WorkflowResult result =
                workflowEngine.execute(plan);

        assertFalse(
                result.isSuccessful()
        );

        assertTrue(
                result.getFailedTasks()
                        .contains(task.getTaskId())
        );

        AgentResult failedResult =
                result.getResults()
                        .get(task.getTaskId());

        assertNotNull(failedResult);

        assertEquals(
                AgentType.CFO,
                failedResult.getAgentType()
        );

        assertEquals(
                AgentResultStatus.FAILURE,
                failedResult.getStatus()
        );

        assertEquals(
                "Agent execution failed",
                failedResult.getError()
        );
    }

    @Test
    void shouldRejectCircularDependencies() {

        UUID taskOneId = UUID.randomUUID();
        UUID taskTwoId = UUID.randomUUID();

        AgentTask taskOne = AgentTask.builder()
                .taskId(taskOneId)
                .agentType(AgentType.CFO)
                .objective("Task one")
                .dependencies(
                        List.of(taskTwoId)
                )
                .build();

        AgentTask taskTwo = AgentTask.builder()
                .taskId(taskTwoId)
                .agentType(AgentType.ENGINEERING)
                .objective("Task two")
                .dependencies(
                        List.of(taskOneId)
                )
                .build();

        ExecutionPlan plan = ExecutionPlan.builder()
                .tasks(
                        List.of(
                                taskOne,
                                taskTwo
                        )
                )
                .build();

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> workflowEngine.execute(plan)
                );

        assertTrue(
                exception.getMessage()
                        .contains("cannot make progress")
        );
    }

    @Test
    void shouldRejectDuplicateTaskIds() {

        UUID duplicateId = UUID.randomUUID();

        AgentTask taskOne = AgentTask.builder()
                .taskId(duplicateId)
                .agentType(AgentType.CFO)
                .objective("First task")
                .build();

        AgentTask taskTwo = AgentTask.builder()
                .taskId(duplicateId)
                .agentType(AgentType.ENGINEERING)
                .objective("Second task")
                .build();

        ExecutionPlan plan = ExecutionPlan.builder()
                .tasks(
                        List.of(
                                taskOne,
                                taskTwo
                        )
                )
                .build();

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> workflowEngine.execute(plan)
                );

        assertTrue(
                exception.getMessage()
                        .contains("Duplicate task ID")
        );
    }

    @Test
    void shouldRejectNullExecutionPlan() {

        assertThrows(
                IllegalArgumentException.class,
                () -> workflowEngine.execute(null)
        );
    }

    @Test
    void shouldRejectEmptyExecutionPlan() {

        ExecutionPlan plan = ExecutionPlan.builder()
                .tasks(List.of())
                .build();

        assertThrows(
                IllegalArgumentException.class,
                () -> workflowEngine.execute(plan)
        );
    }

    @Test
    void shouldRejectTaskWithoutTaskId() {

        AgentTask task = AgentTask.builder()
                .agentType(AgentType.CFO)
                .objective("Missing task ID")
                .build();

        ExecutionPlan plan = ExecutionPlan.builder()
                .tasks(List.of(task))
                .build();

        assertThrows(
                IllegalArgumentException.class,
                () -> workflowEngine.execute(plan)
        );
    }

    private AgentResult successfulResult(
            AgentType agentType) {

        return AgentResult.builder()
                .agentType(agentType)
                .status(AgentResultStatus.SUCCESS)
                .output("Task completed")
                .build();
    }

    @Test
    void shouldBlockDependentTaskWhenDependencyFails() {

        UUID cfoTaskId = UUID.randomUUID();
        UUID engineeringTaskId = UUID.randomUUID();

        AgentTask cfoTask = AgentTask.builder()
                .taskId(cfoTaskId)
                .agentType(AgentType.CFO)
                .objective("Analyze financial impact")
                .build();

        AgentTask engineeringTask = AgentTask.builder()
                .taskId(engineeringTaskId)
                .agentType(AgentType.ENGINEERING)
                .objective("Analyze engineering impact")
                .dependencies(
                        List.of(cfoTaskId)
                )
                .build();

        AgentResult cfoFailure =
                AgentResult.builder()
                        .agentType(AgentType.CFO)
                        .status(AgentResultStatus.FAILURE)
                        .error("CFO analysis failed")
                        .build();

        when(taskDispatcher.dispatch(cfoTask))
                .thenReturn(cfoFailure);

        ExecutionPlan plan = ExecutionPlan.builder()
                .tasks(
                        List.of(
                                cfoTask,
                                engineeringTask
                        )
                )
                .build();

        WorkflowResult result =
                workflowEngine.execute(plan);

        assertFalse(
                result.isSuccessful()
        );

        assertEquals(
                WorkflowTaskStatus.FAILED,
                result.getTaskStatuses()
                        .get(cfoTaskId)
        );

        assertEquals(
                WorkflowTaskStatus.BLOCKED,
                result.getTaskStatuses()
                        .get(engineeringTaskId)
        );

        assertTrue(
                result.getFailedTasks()
                        .contains(cfoTaskId)
        );

        assertTrue(
                result.getBlockedTasks()
                        .contains(engineeringTaskId)
        );

        verify(taskDispatcher)
                .dispatch(cfoTask);

        verify(
                taskDispatcher,
                never()
        ).dispatch(engineeringTask);
    }

    @Test
    void shouldBlockTransitiveDependencies() {

        UUID cfoTaskId = UUID.randomUUID();
        UUID engineeringTaskId = UUID.randomUUID();
        UUID infrastructureTaskId = UUID.randomUUID();

        AgentTask cfoTask = AgentTask.builder()
                .taskId(cfoTaskId)
                .agentType(AgentType.CFO)
                .objective("Analyze financial impact")
                .build();

        AgentTask engineeringTask = AgentTask.builder()
                .taskId(engineeringTaskId)
                .agentType(AgentType.ENGINEERING)
                .objective("Analyze engineering impact")
                .dependencies(
                        List.of(cfoTaskId)
                )
                .build();

        AgentTask infrastructureTask = AgentTask.builder()
                .taskId(infrastructureTaskId)
                .agentType(AgentType.INFRASTRUCTURE)
                .objective("Analyze infrastructure impact")
                .dependencies(
                        List.of(engineeringTaskId)
                )
                .build();

        AgentResult cfoFailure =
                AgentResult.builder()
                        .agentType(AgentType.CFO)
                        .status(AgentResultStatus.FAILURE)
                        .error("CFO analysis failed")
                        .build();

        when(taskDispatcher.dispatch(cfoTask))
                .thenReturn(cfoFailure);

        ExecutionPlan plan = ExecutionPlan.builder()
                .tasks(
                        List.of(
                                cfoTask,
                                engineeringTask,
                                infrastructureTask
                        )
                )
                .build();

        WorkflowResult result =
                workflowEngine.execute(plan);

        assertFalse(
                result.isSuccessful()
        );

        assertEquals(
                WorkflowTaskStatus.FAILED,
                result.getTaskStatuses()
                        .get(cfoTaskId)
        );

        assertEquals(
                WorkflowTaskStatus.BLOCKED,
                result.getTaskStatuses()
                        .get(engineeringTaskId)
        );

        assertEquals(
                WorkflowTaskStatus.BLOCKED,
                result.getTaskStatuses()
                        .get(infrastructureTaskId)
        );

        verify(taskDispatcher)
                .dispatch(cfoTask);

        verify(
                taskDispatcher,
                never()
        ).dispatch(engineeringTask);

        verify(
                taskDispatcher,
                never()
        ).dispatch(infrastructureTask);
    }

    @Test
    void shouldRejectUnknownDependency() {

        UUID unknownDependencyId = UUID.randomUUID();

        AgentTask task = AgentTask.builder()
                .taskId(UUID.randomUUID())
                .agentType(AgentType.ENGINEERING)
                .objective("Analyze engineering requirements")
                .dependencies(
                        List.of(unknownDependencyId)
                )
                .build();

        ExecutionPlan plan = ExecutionPlan.builder()
                .tasks(List.of(task))
                .build();

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> workflowEngine.execute(plan)
                );

        assertTrue(
                exception.getMessage()
                        .contains("depends on unknown task")
        );

        assertTrue(
                exception.getMessage()
                        .contains(
                                unknownDependencyId.toString()
                        )
        );
    }
}