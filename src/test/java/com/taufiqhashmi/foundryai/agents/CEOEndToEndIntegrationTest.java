package com.taufiqhashmi.foundryai.agents;

import com.taufiqhashmi.foundryai.workflows.ExecutionPlan;
import com.taufiqhashmi.foundryai.workflows.WorkflowEngine;
import com.taufiqhashmi.foundryai.workflows.WorkflowResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class CEOEndToEndIntegrationTest {

    private final CEOPlanner ceoPlanner;
    private final WorkflowEngine workflowEngine;
    private final CEOSynthesizer ceoSynthesizer;

    @Autowired
    CEOEndToEndIntegrationTest(
            CEOPlanner ceoPlanner,
            WorkflowEngine workflowEngine,
            CEOSynthesizer ceoSynthesizer) {

        this.ceoPlanner = ceoPlanner;
        this.workflowEngine = workflowEngine;
        this.ceoSynthesizer = ceoSynthesizer;
    }

    @Test
    void shouldExecuteCompleteCeoWorkflow() {

        // ---------------------------------------------------------
        // 1. Business objective
        // ---------------------------------------------------------

        String businessObjective = """
                A fintech startup needs to decide whether to prioritize
                reducing cloud infrastructure costs or accelerating product
                development over the next quarter.

                The decision must be evaluated from three required perspectives:
                1. Financial impact and ROI.
                2. Engineering feasibility, effort, and technical trade-offs.
                3. Infrastructure cost, scalability, reliability, and operational impact.

                Provide a final executive recommendation that considers
                all three perspectives.
                """;

        // ---------------------------------------------------------
        // 2. CEO creates execution plan
        // ---------------------------------------------------------

        ExecutionPlan plan = ceoPlanner.createPlan(
                businessObjective
        );

        assertNotNull(plan);

        assertNotNull(
                plan.getTasks()
        );

        assertFalse(
                plan.getTasks().isEmpty(),
                "CEO should generate specialist tasks"
        );

        System.out.println(
                "========================================"
        );

        System.out.println(
                "CEO GENERATED EXECUTION PLAN"
        );

        System.out.println(
                "========================================"
        );

        plan.getTasks().forEach(task -> {

            System.out.println(
                    "Task ID      : "
                            + task.getTaskId()
            );

            System.out.println(
                    "Agent Type   : "
                            + task.getAgentType()
            );

            System.out.println(
                    "Objective    : "
                            + task.getObjective()
            );

            System.out.println(
                    "Dependencies : "
                            + task.getDependencies()
            );

            System.out.println(
                    "----------------------------------------"
            );

            assertNotNull(
                    task.getTaskId()
            );

            assertNotNull(
                    task.getAgentType()
            );

            assertNotNull(
                    task.getObjective()
            );

            assertFalse(
                    task.getObjective().isBlank()
            );

            assertTrue(
                    task.getAgentType() != AgentType.CEO,
                    "CEO must delegate work to specialist agents"
            );
        });

        // ---------------------------------------------------------
        // Verify that the CEO selected all required specialists
        // for this specific E2E scenario.
        // ---------------------------------------------------------

        Set<AgentType> plannedAgentTypes =
                plan.getTasks()
                        .stream()
                        .map(AgentTask::getAgentType)
                        .collect(Collectors.toSet());

        assertTrue(
                plannedAgentTypes.contains(AgentType.CFO),
                "CEO plan must contain a CFO task"
        );

        assertTrue(
                plannedAgentTypes.contains(AgentType.ENGINEERING),
                "CEO plan must contain an Engineering task"
        );

        assertTrue(
                plannedAgentTypes.contains(AgentType.INFRASTRUCTURE),
                "CEO plan must contain an Infrastructure task"
        );

        // ---------------------------------------------------------
        // 3. Execute specialist workflow
        // ---------------------------------------------------------

        WorkflowResult workflowResult =
                workflowEngine.execute(plan);

        assertNotNull(
                workflowResult
        );

        System.out.println(
                "========================================"
        );

        System.out.println(
                "SPECIALIST WORKFLOW RESULTS"
        );

        System.out.println(
                "========================================"
        );

        workflowResult.getResults()
                .forEach((taskId, result) -> {

                    System.out.println(
                            "Task ID    : "
                                    + taskId
                    );

                    System.out.println(
                            "Agent      : "
                                    + result.getAgentType()
                    );

                    System.out.println(
                            "Status     : "
                                    + result.getStatus()
                    );

                    System.out.println(
                            "Output     : "
                                    + result.getOutput()
                    );

                    if (result.getError() != null) {
                        System.out.println(
                                "Error      : "
                                        + result.getError()
                        );
                    }

                    System.out.println(
                            "----------------------------------------"
                    );
                });

        assertTrue(
                workflowResult.isSuccessful(),
                "Specialist workflow failed"
        );

        assertFalse(
                workflowResult.getResults().isEmpty(),
                "Workflow should produce specialist results"
        );

        assertTrue(
                workflowResult.getResults()
                        .values()
                        .stream()
                        .allMatch(AgentResult::isSuccessful),
                "All specialist tasks should succeed"
        );

        // ---------------------------------------------------------
        // 4. CEO synthesizes specialist results
        // ---------------------------------------------------------

        String finalRecommendation =
                ceoSynthesizer.synthesize(
                        businessObjective,
                        workflowResult.getResults()
                );

        // ---------------------------------------------------------
        // 5. Verify final CEO outcome
        // ---------------------------------------------------------

        assertNotNull(
                finalRecommendation
        );

        assertFalse(
                finalRecommendation.isBlank(),
                "CEO synthesis should return a final recommendation"
        );

        System.out.println(
                "========================================"
        );

        System.out.println(
                "FINAL CEO RECOMMENDATION"
        );

        System.out.println(
                "========================================"
        );

        System.out.println(
                finalRecommendation
        );

        System.out.println(
                "========================================"
        );
    }
}