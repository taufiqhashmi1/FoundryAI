package com.taufiqhashmi.foundryai.ai;

import com.taufiqhashmi.foundryai.agents.AgentResult;
import com.taufiqhashmi.foundryai.agents.AgentType;
import com.taufiqhashmi.foundryai.workflows.PlannedTask;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface StructuredAiModelGateway {

    List<PlannedTask> generatePlan(
            AgentType agentType,
            String userPrompt
    );

    String synthesize(
            AgentType agentType,
            String businessObjective,
            Map<UUID, AgentResult> specialistResults
    );
}