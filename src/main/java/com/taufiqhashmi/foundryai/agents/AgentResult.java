package com.taufiqhashmi.foundryai.agents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentResult {

    private AgentType agentType;

    private AgentResultStatus status;

    /*
     * Human-readable explanation.
     */
    private String output;

    /*
     * Machine-readable findings that other agents can consume.
     */
    private Map<String, Object> structuredData;

    /*
     * Explicit assumptions made by the agent.
     */
    private List<String> assumptions;

    /*
     * Risks identified by the agent.
     */
    private List<String> risks;

    private String error;

    private Map<String, Object> metadata;

    public boolean isSuccessful() {
        return status == AgentResultStatus.SUCCESS;
    }

    public boolean isFailed() {
        return status == AgentResultStatus.FAILURE;
    }
}