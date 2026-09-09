package com.taufiqhashmi.foundryai.agents;

import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class AgentRegistry {

    private final Map<AgentType, Agent> agents;

    public AgentRegistry(List<Agent> agentList) {
        this.agents = new EnumMap<>(AgentType.class);

        for (Agent agent : agentList) {
            AgentType agentType = agent.getType();

            if (agents.containsKey(agentType)) {
                throw new IllegalStateException(
                        "Multiple agents registered for type: " + agentType
                );
            }

            agents.put(agentType, agent);
        }
    }

    public Agent getAgent(AgentType agentType) {
        Agent agent = agents.get(agentType);

        if (agent == null) {
            throw new IllegalArgumentException(
                    "No agent registered for type: " + agentType
            );
        }

        return agent;
    }
}