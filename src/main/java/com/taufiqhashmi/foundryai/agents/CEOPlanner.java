package com.taufiqhashmi.foundryai.agents;

import com.taufiqhashmi.foundryai.ai.StructuredAiModelGateway;
import com.taufiqhashmi.foundryai.workflows.ExecutionPlan;
import com.taufiqhashmi.foundryai.workflows.ExecutionPlanBuilder;
import org.springframework.stereotype.Component;

@Component
public class CEOPlanner {

    private final StructuredAiModelGateway aiModelGateway;
    private final ExecutionPlanBuilder executionPlanBuilder;

    public CEOPlanner(
            StructuredAiModelGateway aiModelGateway,
            ExecutionPlanBuilder executionPlanBuilder
    ) {
        this.aiModelGateway = aiModelGateway;
        this.executionPlanBuilder = executionPlanBuilder;
    }

    public ExecutionPlan createPlan(
            String businessObjective
    ) {
        if (businessObjective == null ||
                businessObjective.isBlank()) {

            throw new IllegalArgumentException(
                    "Business objective cannot be null or blank"
            );
        }

        return executionPlanBuilder.build(
                aiModelGateway.generatePlan(
                        AgentType.CEO,
                        businessObjective
                )
        );
    }
}