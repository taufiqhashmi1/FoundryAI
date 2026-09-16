package com.taufiqhashmi.foundryai.agents.implementations;

import com.taufiqhashmi.foundryai.agents.Agent;
import com.taufiqhashmi.foundryai.agents.AgentResult;
import com.taufiqhashmi.foundryai.agents.AgentResultStatus;
import com.taufiqhashmi.foundryai.agents.AgentTask;
import com.taufiqhashmi.foundryai.agents.AgentType;
import com.taufiqhashmi.foundryai.agents.StructuredAgentResponse;
import com.taufiqhashmi.foundryai.ai.AiModelGateway;

import org.springframework.stereotype.Component;

@Component
public class CEOAgent implements Agent {

    private final AiModelGateway aiModelGateway;

    public CEOAgent(AiModelGateway aiModelGateway) {
        this.aiModelGateway = aiModelGateway;
    }

    @Override
    public AgentType getType() {
        return AgentType.CEO;
    }

    @Override
    public AgentResult execute(AgentTask task) {

        if (task == null) {
            throw new IllegalArgumentException(
                    "Agent task cannot be null"
            );
        }

        if (task.getObjective() == null ||
                task.getObjective().isBlank()) {
            throw new IllegalArgumentException(
                    "Agent task objective cannot be null or blank"
            );
        }

        try {

            StructuredAgentResponse response =
                    aiModelGateway.generateAgentResponse(
                            AgentType.CEO,
                            task.getObjective(),
                            task.getContext()
                    );

            if (response == null ||
                    response.getOutput() == null ||
                    response.getOutput().isBlank()) {

                return failure(
                        "Agent returned an empty output"
                );
            }

            return AgentResult.builder()
                    .agentType(AgentType.CEO)
                    .status(AgentResultStatus.SUCCESS)
                    .output(response.getOutput())
                    .structuredData(response.getStructuredData())
                    .assumptions(response.getAssumptions())
                    .risks(response.getRisks())
                    .build();

        } catch (Exception exception) {

            return failure(exception.getMessage());
        }
    }

    private AgentResult failure(String error) {

        return AgentResult.builder()
                .agentType(AgentType.CEO)
                .status(AgentResultStatus.FAILURE)
                .error(error)
                .build();
    }
}