package com.taufiqhashmi.foundryai.workflows;

import com.taufiqhashmi.foundryai.agents.Agent;
import com.taufiqhashmi.foundryai.agents.AgentRegistry;
import com.taufiqhashmi.foundryai.agents.AgentResult;
import com.taufiqhashmi.foundryai.agents.AgentTask;
import org.springframework.stereotype.Component;

@Component
public class WorkflowTaskDispatcher {

    private final AgentRegistry agentRegistry;

    public WorkflowTaskDispatcher(
            AgentRegistry agentRegistry
    ) {
        this.agentRegistry = agentRegistry;
    }

    public AgentResult dispatch(
            AgentTask task
    ) {
        if (task == null) {
            throw new IllegalArgumentException(
                    "Agent task cannot be null"
            );
        }

        if (task.getAgentType() == null) {
            throw new IllegalArgumentException(
                    "Agent type cannot be null"
            );
        }

        Agent agent =
                agentRegistry.getAgent(task.getAgentType());

        return agent.execute(task);
    }
}