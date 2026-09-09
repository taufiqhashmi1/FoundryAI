package com.taufiqhashmi.foundryai.agents;

public interface Agent {

    AgentType getType();

    AgentResult execute(AgentTask task);
}