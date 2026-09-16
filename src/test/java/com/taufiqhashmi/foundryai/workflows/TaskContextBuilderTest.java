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
                .structuredData(Map.of(
                        "revenue", 100000
                ))
                .assumptions(List.of("Stable demand"))
                .risks(List.of("Market volatility"))
                .build();

        AgentTask task = AgentTask.builder()
                .taskId(UUID.randomUUID())
                .agentType(AgentType.ENGINEERING)
                .objective("Use the financial analysis.")
                .dependencies(List.of(dependencyId))
                .build();

        AgentContext context = taskContextBuilder.build(
                task,
                Map.of(dependencyId, dependencyResult)
        );

        assertNotNull(context);
        assertNotNull(context.getData());

        Object dependencyResults =
                context.getData().get("dependencyResults");

        assertNotNull(dependencyResults);
        assertTrue(dependencyResults instanceof Map<?, ?>);

        @SuppressWarnings("unchecked")
        Map<UUID, Map<String, Object>> results =
                (Map<UUID, Map<String, Object>>) dependencyResults;

        Map<String, Object> resultData =
                results.get(dependencyId);

        assertNotNull(resultData);

        assertEquals(
                AgentType.CFO,
                resultData.get("agentType")
        );

        assertEquals(
                "Financial analysis completed.",
                resultData.get("output")
        );

        assertEquals(
                Map.of("revenue", 100000),
                resultData.get("structuredData")
        );

        assertEquals(
                List.of("Stable demand"),
                resultData.get("assumptions")
        );

        assertEquals(
                List.of("Market volatility"),
                resultData.get("risks")
        );
    }

    @Test
    void shouldReturnEmptyDependencyResultsWhenNoDependencies() {

        AgentTask task = AgentTask.builder()
                .taskId(UUID.randomUUID())
                .agentType(AgentType.CFO)
                .objective("Perform financial analysis.")
                .dependencies(List.of())
                .build();

        AgentContext context = taskContextBuilder.build(
                task,
                Map.of()
        );

        assertNotNull(context);
        assertNotNull(context.getData());

        Object dependencyResults =
                context.getData().get("dependencyResults");

        assertNotNull(dependencyResults);
        assertTrue(dependencyResults instanceof Map<?, ?>);

        @SuppressWarnings("unchecked")
        Map<UUID, Object> results =
                (Map<UUID, Object>) dependencyResults;

        assertTrue(results.isEmpty());
    }

    @Test
    void shouldIgnoreMissingDependencyResult() {

        UUID dependencyId = UUID.randomUUID();

        AgentTask task = AgentTask.builder()
                .taskId(UUID.randomUUID())
                .agentType(AgentType.ENGINEERING)
                .objective("Use the financial analysis.")
                .dependencies(List.of(dependencyId))
                .build();

        AgentContext context = taskContextBuilder.build(
                task,
                Map.of()
        );

        assertNotNull(context);
        assertNotNull(context.getData());

        Object dependencyResults =
                context.getData().get("dependencyResults");

        assertNotNull(dependencyResults);
        assertTrue(dependencyResults instanceof Map<?, ?>);

        @SuppressWarnings("unchecked")
        Map<UUID, Object> results =
                (Map<UUID, Object>) dependencyResults;

        assertTrue(results.isEmpty());
    }

    @Test
    void shouldNotPreserveExistingContext() {

        AgentTask task = AgentTask.builder()
                .taskId(UUID.randomUUID())
                .agentType(AgentType.CFO)
                .objective("Analyze financial impact.")
                .context(
                        AgentContext.builder()
                                .data(
                                        Map.of(
                                                "requestId",
                                                "REQ-123"
                                        )
                                )
                                .build()
                )
                .build();

        AgentContext context = taskContextBuilder.build(
                task,
                Map.of()
        );

        assertNotNull(context);
        assertNotNull(context.getData());

        assertTrue(
                context.getData()
                        .get("requestId") == null
        );

        assertNotNull(
                context.getData()
                        .get("dependencyResults")
        );
    }
}