package com.taufiqhashmi.foundryai.agents;

import com.taufiqhashmi.foundryai.ai.StructuredAiModelGateway;
import com.taufiqhashmi.foundryai.workflows.ExecutionPlan;
import com.taufiqhashmi.foundryai.workflows.TaskContextBuilder;
import com.taufiqhashmi.foundryai.workflows.TaskDependencyResolver;
import com.taufiqhashmi.foundryai.workflows.WorkflowEngine;
import com.taufiqhashmi.foundryai.workflows.WorkflowResult;
import com.taufiqhashmi.foundryai.workflows.WorkflowTaskDispatcher;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CEOOrchestrationTest {

    @Test
    void shouldExecuteSpecialistsAndSynthesizeResults() {

        // ---------------------------------------------------------
        // 1. Business objective
        // ---------------------------------------------------------

        String businessObjective = """
                A fintech startup needs to decide whether to prioritize
                reducing cloud infrastructure costs or accelerating product
                development over the next quarter.
                """;

        // ---------------------------------------------------------
        // 2. Create a deterministic execution plan
        //
        // CEOPlanner and ExecutionPlanBuilder are already tested
        // separately. Here we use a known valid plan so this test
        // focuses on orchestration.
        // ---------------------------------------------------------

        CEOPlanner planner =
                mock(CEOPlanner.class);

        ExecutionPlan plan =
                createExecutionPlan();

        when(
                planner.createPlan(businessObjective)
        ).thenReturn(plan);

        ExecutionPlan generatedPlan =
                planner.createPlan(businessObjective);

        assertNotNull(generatedPlan);
        assertFalse(
                generatedPlan.getTasks().isEmpty()
        );

        // ---------------------------------------------------------
        // 3. Create specialist agents
        // ---------------------------------------------------------

        Agent cfoAgent =
                mock(Agent.class);

        Agent engineeringAgent =
                mock(Agent.class);

        Agent infrastructureAgent =
                mock(Agent.class);

        when(cfoAgent.getType())
                .thenReturn(AgentType.CFO);

        when(engineeringAgent.getType())
                .thenReturn(AgentType.ENGINEERING);

        when(infrastructureAgent.getType())
                .thenReturn(AgentType.INFRASTRUCTURE);

        when(cfoAgent.execute(any(AgentTask.class)))
                .thenReturn(
                        AgentResult.builder()
                                .agentType(AgentType.CFO)
                                .status(
                                        AgentResultStatus.SUCCESS
                                )
                                .output(
                                        "Cloud cost reduction provides "
                                                + "the strongest immediate "
                                                + "financial benefit."
                                )
                                .build()
                );

        when(engineeringAgent.execute(any(AgentTask.class)))
                .thenReturn(
                        AgentResult.builder()
                                .agentType(
                                        AgentType.ENGINEERING
                                )
                                .status(
                                        AgentResultStatus.SUCCESS
                                )
                                .output(
                                        "Accelerating development requires "
                                                + "additional engineering capacity."
                                )
                                .build()
                );

        when(infrastructureAgent.execute(
                any(AgentTask.class)
        )).thenReturn(
                AgentResult.builder()
                        .agentType(
                                AgentType.INFRASTRUCTURE
                        )
                        .status(
                                AgentResultStatus.SUCCESS
                        )
                        .output(
                                "Infrastructure optimization can reduce "
                                        + "cloud expenditure without major "
                                        + "architectural changes."
                        )
                        .build()
        );

        // ---------------------------------------------------------
        // 4. Build real workflow runtime
        // ---------------------------------------------------------

        AgentRegistry agentRegistry =
                new AgentRegistry(
                        List.of(
                                cfoAgent,
                                engineeringAgent,
                                infrastructureAgent
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

        // ---------------------------------------------------------
        // 5. Execute workflow
        // ---------------------------------------------------------

        WorkflowResult workflowResult =
                workflowEngine.execute(
                        generatedPlan
                );

        // ---------------------------------------------------------
        // 6. Verify specialist execution
        // ---------------------------------------------------------

        assertNotNull(workflowResult);

        assertTrue(
                workflowResult.isSuccessful(),
                "Workflow should complete successfully"
        );

        assertEquals(
                3,
                workflowResult.getResults().size()
        );

        assertTrue(
                workflowResult.getResults()
                        .values()
                        .stream()
                        .allMatch(
                                AgentResult::isSuccessful
                        ),
                "All specialist agents should succeed"
        );

        // ---------------------------------------------------------
        // 7. CEO synthesis
        //
        // The LLM boundary is mocked. CEOSynthesizer itself
        // remains real.
        // ---------------------------------------------------------

        StructuredAiModelGateway gateway =
                mock(StructuredAiModelGateway.class);

        CEOSynthesizer synthesizer =
                new CEOSynthesizer(gateway);

        String expectedRecommendation =
                "Prioritize cloud cost reduction while "
                        + "maintaining focused product development.";

        when(
                gateway.synthesize(
                        AgentType.CEO,
                        businessObjective,
                        workflowResult.getResults()
                )
        ).thenReturn(
                expectedRecommendation
        );

        String finalRecommendation =
                synthesizer.synthesize(
                        businessObjective,
                        workflowResult.getResults()
                );

        // ---------------------------------------------------------
        // 8. Verify final CEO result
        // ---------------------------------------------------------

        assertNotNull(finalRecommendation);

        assertFalse(
                finalRecommendation.isBlank()
        );

        assertEquals(
                expectedRecommendation,
                finalRecommendation
        );
    }

    private ExecutionPlan createExecutionPlan() {

        AgentTask financialTask =
                AgentTask.builder()
                        .taskId(
                                java.util.UUID.randomUUID()
                        )
                        .agentType(
                                AgentType.CFO
                        )
                        .objective(
                                "Analyze the financial impact of "
                                        + "cloud cost reduction versus "
                                        + "accelerated product development."
                        )
                        .dependencies(
                                List.of()
                        )
                        .build();

        AgentTask engineeringTask =
                AgentTask.builder()
                        .taskId(
                                java.util.UUID.randomUUID()
                        )
                        .agentType(
                                AgentType.ENGINEERING
                        )
                        .objective(
                                "Analyze the engineering impact of "
                                        + "accelerating product development."
                        )
                        .dependencies(
                                List.of()
                        )
                        .build();

        AgentTask infrastructureTask =
                AgentTask.builder()
                        .taskId(
                                java.util.UUID.randomUUID()
                        )
                        .agentType(
                                AgentType.INFRASTRUCTURE
                        )
                        .objective(
                                "Analyze infrastructure opportunities "
                                        + "for reducing cloud costs."
                        )
                        .dependencies(
                                List.of()
                        )
                        .build();

        return ExecutionPlan.builder()
                .tasks(
                        List.of(
                                financialTask,
                                engineeringTask,
                                infrastructureTask
                        )
                )
                .build();
    }
}