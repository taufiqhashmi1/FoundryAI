package com.taufiqhashmi.foundryai.ai;

import com.taufiqhashmi.foundryai.agents.AgentContext;
import com.taufiqhashmi.foundryai.agents.AgentType;
import com.taufiqhashmi.foundryai.agents.StructuredAgentResponse;
import com.taufiqhashmi.foundryai.dtos.CEORecommendationResponseDTO;
import com.taufiqhashmi.foundryai.workflows.PlannedTask;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AiModelGateway {

    List<PlannedTask> generatePlan(
            AgentType agentType,
            String userPrompt
    );

    StructuredAgentResponse generateAgentResponse(
            AgentType agentType,
            String userPrompt,
            AgentContext context
    );

    CEORecommendationResponseDTO synthesize(
            AgentType agentType,
            String businessObjective,
            Map<UUID, com.taufiqhashmi.foundryai.agents.AgentResult> specialistResults
    );
}