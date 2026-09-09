package com.taufiqhashmi.foundryai.workflows;

import com.taufiqhashmi.foundryai.agents.AgentResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowResult {

    private boolean successful;

    private Map<UUID, AgentResult> results;

    private Map<UUID, WorkflowTaskStatus> taskStatuses;

    private List<UUID> failedTasks;

    private List<UUID> blockedTasks;

    public boolean isFailed() {
        return !successful;
    }
}