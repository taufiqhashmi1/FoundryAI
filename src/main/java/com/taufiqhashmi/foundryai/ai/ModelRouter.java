package com.taufiqhashmi.foundryai.ai;

import com.taufiqhashmi.foundryai.agents.AgentConfig;
import com.taufiqhashmi.foundryai.agents.AgentType;
import org.springframework.stereotype.Component;

@Component
public class ModelRouter {

    private final AgentConfig agentConfig;

    public ModelRouter(AgentConfig agentConfig) {
        this.agentConfig = agentConfig;
    }

    public AgentConfig.AgentSettings resolve(AgentType agentType) {

        if (agentType == null) {
            throw new IllegalArgumentException(
                    "Agent type cannot be null"
            );
        }

        AgentConfig.AgentSettings settings = switch (agentType) {
            case CEO -> agentConfig.getCeo();
            case CFO -> agentConfig.getCfo();
            case ENGINEERING -> agentConfig.getEngineering();
            case INFRASTRUCTURE -> agentConfig.getInfrastructure();
        };

        if (settings == null) {
            throw new IllegalStateException(
                    "No AI configuration found for agent type: " + agentType
            );
        }

        validate(settings, agentType);

        return settings;
    }

    private void validate(
            AgentConfig.AgentSettings settings,
            AgentType agentType
    ) {
        if (settings.getModel() == null ||
                settings.getModel().isBlank()) {

            throw new IllegalStateException(
                    "No model configured for agent type: " + agentType
            );
        }

        if (settings.getSystemPrompt() == null ||
                settings.getSystemPrompt().isBlank()) {

            throw new IllegalStateException(
                    "No system prompt configured for agent type: " + agentType
            );
        }
    }
}