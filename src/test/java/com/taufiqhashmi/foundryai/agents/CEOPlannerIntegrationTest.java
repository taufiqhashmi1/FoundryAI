package com.taufiqhashmi.foundryai.agents;

import com.taufiqhashmi.foundryai.workflows.ExecutionPlan;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class CEOPlannerIntegrationTest {

    private final CEOPlanner ceoPlanner;

    @Autowired
    CEOPlannerIntegrationTest(
            CEOPlanner ceoPlanner) {
        this.ceoPlanner = ceoPlanner;
    }

    @Test
    void shouldGenerateStructuredExecutionPlan() {

        String businessObjective = """
                A fintech startup needs to decide whether to prioritize
                reducing cloud infrastructure costs or accelerating product
                development over the next quarter.

                Analyze the objective from financial, engineering,
                and infrastructure perspectives and create an executable
                plan for the specialist agents.
                """;

        ExecutionPlan plan = ceoPlanner.createPlan(
                businessObjective);

        assertNotNull(plan);
        assertNotNull(plan.getTasks());

        assertFalse(
                plan.getTasks().isEmpty(),
                "CEO should generate at least one task");

        System.out.println(
                "========================================");

        System.out.println(
                "CEO STRUCTURED EXECUTION PLAN");

        System.out.println(
                "========================================");

        for (var task : plan.getTasks()) {

            System.out.println(
                    "Task ID      : "
                            + task.getTaskId());

            System.out.println(
                    "Agent Type   : "
                            + task.getAgentType());

            System.out.println(
                    "Objective    : "
                            + task.getObjective());

            System.out.println(
                    "Dependencies : "
                            + task.getDependencies());

            System.out.println(
                    "----------------------------------------");

            assertNotNull(
                    task.getTaskId());

            assertNotNull(
                    task.getAgentType());

            assertNotNull(
                    task.getObjective());

            assertFalse(
                    task.getObjective().isBlank());
        }

        assertTrue(
                plan.getTasks()
                        .stream()
                        .allMatch(task -> task.getAgentType() != AgentType.CEO),
                "CEO should delegate work to specialist agents");
    }
}