package com.taufiqhashmi.foundryai.ai;

import com.taufiqhashmi.foundryai.agents.AgentType;

public interface AiModelGateway {

    String generate(
            AgentType agentType,
            String userPrompt
    );
}