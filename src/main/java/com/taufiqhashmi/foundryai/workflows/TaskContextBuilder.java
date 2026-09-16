package com.taufiqhashmi.foundryai.workflows;

import com.taufiqhashmi.foundryai.agents.AgentContext;
import com.taufiqhashmi.foundryai.agents.AgentResult;
import com.taufiqhashmi.foundryai.agents.AgentTask;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TaskContextBuilder {

        public AgentContext build(
                        AgentTask task,
                        Map<UUID, AgentResult> results) {
                if (task == null) {
                        throw new IllegalArgumentException(
                                        "Agent task cannot be null");
                }

                Map<String, Object> contextData = new HashMap<>();

                Map<UUID, Object> dependencyResults = new HashMap<>();

                if (task.getDependencies() != null &&
                                results != null) {

                        for (UUID dependencyId : task.getDependencies()) {

                                AgentResult result = results.get(dependencyId);

                                if (result != null) {

                                        Map<String, Object> resultData = new HashMap<>();

                                        resultData.put(
                                                        "agentType",
                                                        result.getAgentType());

                                        resultData.put(
                                                        "output",
                                                        result.getOutput());

                                        resultData.put(
                                                        "structuredData",
                                                        result.getStructuredData());

                                        resultData.put(
                                                        "assumptions",
                                                        result.getAssumptions());

                                        resultData.put(
                                                        "risks",
                                                        result.getRisks());

                                        dependencyResults.put(
                                                        dependencyId,
                                                        resultData);
                                }
                        }
                }

                contextData.put(
                                "dependencyResults",
                                dependencyResults);

                return AgentContext.builder()
                                .data(contextData)
                                .build();
        }
}