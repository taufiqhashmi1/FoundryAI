package com.taufiqhashmi.foundryai.agents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentResult {

    private AgentType agentType;

    private AgentResultStatus status;

    private String output;

    private String error;

    private Map<String, Object> metadata;

    public boolean isSuccessful() {
        return status == AgentResultStatus.SUCCESS;
    }

    public boolean isFailed() {
        return status == AgentResultStatus.FAILURE;
    }
}