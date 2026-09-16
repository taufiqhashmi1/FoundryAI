package com.taufiqhashmi.foundryai.agents.implementations;

import com.taufiqhashmi.foundryai.agents.AgentResult;
import com.taufiqhashmi.foundryai.agents.AgentResultStatus;
import com.taufiqhashmi.foundryai.agents.AgentTask;
import com.taufiqhashmi.foundryai.agents.AgentType;
import com.taufiqhashmi.foundryai.agents.StructuredAgentResponse;
import com.taufiqhashmi.foundryai.ai.AiModelGateway;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EngineeringAgentTest {

    @Test
    void shouldFailWhenAiModelReturnsEmptyOutput() {

        AiModelGateway aiModelGateway =
                mock(AiModelGateway.class);

        when(aiModelGateway.generateAgentResponse(
                eq(AgentType.ENGINEERING),
                anyString(),
                any()
        )).thenReturn(
                StructuredAgentResponse.builder()
                        .output("")
                        .build()
        );

        EngineeringAgent engineeringAgent =
                new EngineeringAgent(aiModelGateway);

        AgentTask task = AgentTask.builder()
                .agentType(AgentType.ENGINEERING)
                .objective("Assess engineering effort")
                .build();

        AgentResult result =
                engineeringAgent.execute(task);

        assertEquals(
                AgentResultStatus.FAILURE,
                result.getStatus()
        );

        assertEquals(
                "Agent returned an empty output",
                result.getError()
        );

        assertNull(result.getOutput());
    }

    @Test
    void shouldFailWhenAiModelReturnsNullOutput() {

        AiModelGateway aiModelGateway =
                mock(AiModelGateway.class);

        when(aiModelGateway.generateAgentResponse(
                eq(AgentType.ENGINEERING),
                anyString(),
                any()
        )).thenReturn(
                StructuredAgentResponse.builder()
                        .output(null)
                        .build()
        );

        EngineeringAgent engineeringAgent =
                new EngineeringAgent(aiModelGateway);

        AgentTask task = AgentTask.builder()
                .agentType(AgentType.ENGINEERING)
                .objective("Assess engineering effort")
                .build();

        AgentResult result =
                engineeringAgent.execute(task);

        assertEquals(
                AgentResultStatus.FAILURE,
                result.getStatus()
        );

        assertEquals(
                "Agent returned an empty output",
                result.getError()
        );

        assertNull(result.getOutput());
    }
}