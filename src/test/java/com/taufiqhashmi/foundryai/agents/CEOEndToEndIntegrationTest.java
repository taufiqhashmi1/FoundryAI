package com.taufiqhashmi.foundryai.agents;

import com.taufiqhashmi.foundryai.dtos.CEORecommendationResponseDTO;
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
                you are the ceo of roboAI which make autonomous robots
                for security. one robot cost 15000 to make and we have
                to make atleast 30 laks in profit to not be in loss.
                also we have to make a new engineering system for
                security of the bot because the bot is connected to
                the internet it is prone to cyberattacks and also we
                need to have a plan to deploy these features. give me
                the financial,engineering and infrastucture plan for
                executing all these functions
                """;

        // ---------------------------------------------------------
        // 2. CEO creates execution plan
        // ---------------------------------------------------------

        ExecutionPlan plan =
                ceoPlanner.createPlan(businessObjective);

        assertNotNull(plan);
        assertNotNull(plan.getTasks());

        assertFalse(
                plan.getTasks().isEmpty(),
                "CEO should generate specialist tasks"
        );

        System.out.println("========================================");
        System.out.println("CEO GENERATED EXECUTION PLAN");
        System.out.println("========================================");

        plan.getTasks().forEach(task -> {

            System.out.println(
                    "Task ID      : " + task.getTaskId()
            );

            System.out.println(
                    "Agent Type   : " + task.getAgentType()
            );

            System.out.println(
                    "Objective    : " + task.getObjective()
            );

            System.out.println(
                    "Dependencies : " + task.getDependencies()
            );

            System.out.println("----------------------------------------");

            assertNotNull(task.getTaskId());

            assertNotNull(task.getAgentType());

            assertNotNull(task.getObjective());

            assertFalse(
                    task.getObjective().isBlank()
            );

            assertTrue(
                    task.getAgentType() != AgentType.CEO,
                    "CEO must delegate work to specialist agents"
            );
        });

        // ---------------------------------------------------------
        // Verify required specialists
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

        assertNotNull(workflowResult);

        System.out.println("========================================");
        System.out.println("SPECIALIST WORKFLOW RESULTS");
        System.out.println("========================================");

        workflowResult.getResults()
                .forEach((taskId, result) -> {

                    System.out.println(
                            "Task ID    : " + taskId
                    );

                    System.out.println(
                            "Agent      : " + result.getAgentType()
                    );

                    System.out.println(
                            "Status     : " + result.getStatus()
                    );

                    System.out.println(
                            "Output     : " + result.getOutput()
                    );

                    System.out.println(
                            "Structured : " + result.getStructuredData()
                    );

                    System.out.println(
                            "Assumptions: " + result.getAssumptions()
                    );

                    System.out.println(
                            "Risks      : " + result.getRisks()
                    );

                    if (result.getError() != null) {

                        System.out.println(
                                "Error      : " + result.getError()
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
        // Verify structured specialist responses
        // ---------------------------------------------------------

        workflowResult.getResults()
                .values()
                .forEach(result -> {

                    assertNotNull(
                            result.getOutput(),
                            "Successful agent must have output"
                    );

                    assertFalse(
                            result.getOutput().isBlank(),
                            "Successful agent output must not be blank"
                    );

                    assertNotNull(
                            result.getStructuredData(),
                            "Successful agent must provide structured data"
                    );

                    assertNotNull(
                            result.getAssumptions(),
                            "Agent assumptions must not be null"
                    );

                    assertNotNull(
                            result.getRisks(),
                            "Agent risks must not be null"
                    );
                });

        // ---------------------------------------------------------
        // 4. CEO synthesizes specialist results
        // ---------------------------------------------------------

        CEORecommendationResponseDTO finalRecommendation =
                ceoSynthesizer.synthesize(
                        businessObjective,
                        workflowResult.getResults()
                );

        // ---------------------------------------------------------
        // 5. Verify final CEO outcome
        // ---------------------------------------------------------

        assertNotNull(
                finalRecommendation,
                "CEO synthesis should return a recommendation"
        );

        assertNotNull(
                finalRecommendation.getRecommendation(),
                "CEO recommendation text must not be null"
        );

        assertFalse(
                finalRecommendation
                        .getRecommendation()
                        .isBlank(),
                "CEO recommendation text must not be blank"
        );

        assertNotNull(
                finalRecommendation.getKeyFindings(),
                "CEO key findings must not be null"
        );

        assertNotNull(
                finalRecommendation.getAssumptions(),
                "CEO assumptions must not be null"
        );

        assertNotNull(
                finalRecommendation.getRisks(),
                "CEO risks must not be null"
        );

        assertNotNull(
                finalRecommendation.getNextSteps(),
                "CEO next steps must not be null"
        );

        System.out.println("========================================");
        System.out.println("FINAL CEO RECOMMENDATION");
        System.out.println("========================================");

        System.out.println(
                "Recommendation : "
                        + finalRecommendation.getRecommendation()
        );

        System.out.println(
                "Key Findings   : "
                        + finalRecommendation.getKeyFindings()
        );

        System.out.println(
                "Assumptions    : "
                        + finalRecommendation.getAssumptions()
        );

        System.out.println(
                "Risks          : "
                        + finalRecommendation.getRisks()
        );

        System.out.println(
                "Next Steps     : "
                        + finalRecommendation.getNextSteps()
        );

        System.out.println("========================================");
    }
}