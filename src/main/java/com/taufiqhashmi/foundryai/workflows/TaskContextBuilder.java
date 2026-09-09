package com.taufiqhashmi.foundryai.workflows;

import com.taufiqhashmi.foundryai.agents.AgentContext;
import com.taufiqhashmi.foundryai.agents.AgentResult;
import com.taufiqhashmi.foundryai.agents.AgentTask;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class TaskContextBuilder {

    public AgentContext build(
            AgentTask task,
            Map<UUID, AgentResult> completedResults
    ) {
        if (task == null) {
            throw new IllegalArgumentException(
                    "Agent task cannot be null"
            );
        }

        Map<UUID, AgentResult> results =
                completedResults == null
                        ? Map.of()
                        : completedResults;

        Map<String, Object> contextData =
                new HashMap<>();

        addExistingContext(
                task,
                contextData
        );

        addDependencyResults(
                task,
                results,
                contextData
        );

        return AgentContext.builder()
                .data(contextData)
                .build();
    }

    private void addExistingContext(
            AgentTask task,
            Map<String, Object> contextData
    ) {
        if (task.getContext() == null ||
                task.getContext().getData() == null ||
                task.getContext().getData().isEmpty()) {

            return;
        }

        contextData.putAll(
                task.getContext().getData()
        );
    }

    private void addDependencyResults(
            AgentTask task,
            Map<UUID, AgentResult> completedResults,
            Map<String, Object> contextData
    ) {
        List<UUID> dependencies =
                task.getDependencies();

        if (dependencies == null ||
                dependencies.isEmpty()) {

            return;
        }

        Map<UUID, AgentResult> dependencyResults =
                new HashMap<>();

        for (UUID dependencyId : dependencies) {

            if (dependencyId == null) {
                throw new IllegalArgumentException(
                        "Task "
                                + task.getTaskId()
                                + " contains a null dependency"
                );
            }

            AgentResult result =
                    completedResults.get(dependencyId);

            if (result == null) {
                throw new IllegalStateException(
                        "No completed result found for dependency: "
                                + dependencyId
                );
            }

            dependencyResults.put(
                    dependencyId,
                    result
            );
        }

        contextData.put(
                "dependencyResults",
                dependencyResults
        );
    }
}