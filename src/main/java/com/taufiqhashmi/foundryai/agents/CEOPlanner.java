package com.taufiqhashmi.foundryai.agents;

import com.taufiqhashmi.foundryai.ai.AiModelGateway;
import com.taufiqhashmi.foundryai.workflows.ExecutionPlan;
import com.taufiqhashmi.foundryai.workflows.ExecutionPlanBuilder;
import org.springframework.stereotype.Component;

@Component
public class CEOPlanner {

    private final AiModelGateway aiModelGateway;
    private final ExecutionPlanBuilder executionPlanBuilder;

    public CEOPlanner(
            AiModelGateway aiModelGateway,
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