package com.taufiqhashmi.foundryai.workflows;

import com.taufiqhashmi.foundryai.agents.AgentContext;
import com.taufiqhashmi.foundryai.agents.AgentResult;
import com.taufiqhashmi.foundryai.agents.AgentResultStatus;
import com.taufiqhashmi.foundryai.agents.AgentTask;
import com.taufiqhashmi.foundryai.agents.AgentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskContextBuilderTest {

    private TaskContextBuilder taskContextBuilder;

    @BeforeEach
    void setUp() {
        taskContextBuilder = new TaskContextBuilder();
    }

    @Test
    void shouldBuildContextFromDependencyResults() {

        UUID dependencyId = UUID.randomUUID();

        AgentResult dependencyResult = AgentResult.builder()
                .agentType(AgentType.CFO)
                .status(AgentResultStatus.SUCCESS)
                .output("Financial analysis completed.")
                .build();

        AgentTask task = AgentTask.builder()
                .taskId(UUID.randomUUID())
                .agentType(AgentType.ENGINEERING)
                .objective(
                        "Use the financial analysis.")
                .dependencies(
                        List.of(dependencyId))
                .build();

        AgentContext context = taskContextBuilder.build(
                task,
                Map.of(
                        dependencyId,
                        dependencyResult));

        assertNotNull(context);
        assertNotNull(context.getData());

        Object dependencyResults = context.getData()
                .get("dependencyResults");

        assertNotNull(dependencyResults);

        @SuppressWarnings("unchecked")
        Map<UUID, AgentResult> results = (Map<UUID, AgentResult>) dependencyResults;

        assertEquals(
                dependencyResult,
                results.get(dependencyId));
    }

    @Test
    void shouldReturnEmptyContextWhenNoDependencies() {

        AgentTask task = AgentTask.builder()
                .taskId(UUID.randomUUID())
                .agentType(AgentType.CFO)
                .objective(
                        "Perform financial analysis.")
                .dependencies(List.of())
                .build();

        AgentContext context = taskContextBuilder.build(
                task,
                Map.of());

        assertNotNull(context);
        assertNotNull(context.getData());

        assertTrue(
                context.getData().isEmpty());
    }

    @Test
    void shouldRejectMissingDependencyResult() {

        UUID dependencyId = UUID.randomUUID();

        AgentTask task = AgentTask.builder()
                .taskId(UUID.randomUUID())
                .agentType(AgentType.ENGINEERING)
                .objective(
                        "Use the financial analysis.")
                .dependencies(
                        List.of(dependencyId))
                .build();

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> taskContextBuilder.build(
                        task,
                        Map.of()));

        assertTrue(
                exception.getMessage()
                        .contains(
                                "No completed result found"));
    }

    @Test
    void shouldPreserveExistingContext() {

        AgentTask task = AgentTask.builder()
                .taskId(UUID.randomUUID())
                .agentType(AgentType.CFO)
                .objective(
                        "Analyze financial impact.")
                .context(
                        AgentContext.builder()
                                .data(
                                        Map.of(
                                                "requestId",
                                                "REQ-123"))
                                .build())
                .build();

        AgentContext context = taskContextBuilder.build(
                task,
                Map.of());

        assertEquals(
                "REQ-123",
                context.getData()
                        .get("requestId"));
    }
}