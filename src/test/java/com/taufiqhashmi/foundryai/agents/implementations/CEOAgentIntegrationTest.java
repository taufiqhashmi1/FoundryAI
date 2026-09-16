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

class CEOAgentIntegrationTest {

    @Test
    void shouldFailWhenAiModelReturnsNullResponse() {

        AiModelGateway aiModelGateway =
                mock(AiModelGateway.class);

        when(aiModelGateway.generateAgentResponse(
                eq(AgentType.CEO),
                anyString(),
                any()
        )).thenReturn(null);

        CEOAgent ceoAgent =
                new CEOAgent(aiModelGateway);

        AgentTask task = AgentTask.builder()
                .agentType(AgentType.CEO)
                .objective("Create a business strategy")
                .build();

        AgentResult result =
                ceoAgent.execute(task);

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
    void shouldFailWhenAiModelReturnsEmptyOutput() {

        AiModelGateway aiModelGateway =
                mock(AiModelGateway.class);

        when(aiModelGateway.generateAgentResponse(
                eq(AgentType.CEO),
                anyString(),
                any()
        )).thenReturn(
                StructuredAgentResponse.builder()
                        .output("")
                        .build()
        );

        CEOAgent ceoAgent =
                new CEOAgent(aiModelGateway);

        AgentTask task = AgentTask.builder()
                .agentType(AgentType.CEO)
                .objective("Create a business strategy")
                .build();

        AgentResult result =
                ceoAgent.execute(task);

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
    void shouldReturnStructuredAgentResult() {

        AiModelGateway aiModelGateway =
                mock(AiModelGateway.class);

        StructuredAgentResponse response =
                StructuredAgentResponse.builder()
                        .output("Launch the product after validating demand.")
                        .structuredData(
                                java.util.Map.of(
                                        "recommendation",
                                        "Validate demand before launch"
                                )
                        )
                        .assumptions(
                                java.util.List.of(
                                        "Target market exists"
                                )
                        )
                        .risks(
                                java.util.List.of(
                                        "Demand may be lower than expected"
                                )
                        )
                        .build();

        when(aiModelGateway.generateAgentResponse(
                eq(AgentType.CEO),
                anyString(),
                any()
        )).thenReturn(response);

        CEOAgent ceoAgent =
                new CEOAgent(aiModelGateway);

        AgentTask task = AgentTask.builder()
                .agentType(AgentType.CEO)
                .objective("Create a business strategy")
                .build();

        AgentResult result =
                ceoAgent.execute(task);

        assertEquals(
                AgentResultStatus.SUCCESS,
                result.getStatus()
        );

        assertEquals(
                AgentType.CEO,
                result.getAgentType()
        );

        assertEquals(
                "Launch the product after validating demand.",
                result.getOutput()
        );

        assertEquals(
                response.getStructuredData(),
                result.getStructuredData()
        );

        assertEquals(
                response.getAssumptions(),
                result.getAssumptions()
        );

        assertEquals(
                response.getRisks(),
                result.getRisks()
        );
    }

    @Test
    void shouldFailWhenTaskIsNull() {

        AiModelGateway aiModelGateway =
                mock(AiModelGateway.class);

        CEOAgent ceoAgent =
                new CEOAgent(aiModelGateway);

        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> ceoAgent.execute(null)
        );
    }

    @Test
    void shouldFailWhenTaskObjectiveIsBlank() {

        AiModelGateway aiModelGateway =
                mock(AiModelGateway.class);

        CEOAgent ceoAgent =
                new CEOAgent(aiModelGateway);

        AgentTask task = AgentTask.builder()
                .agentType(AgentType.CEO)
                .objective("   ")
                .build();

        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> ceoAgent.execute(task)
        );
    }
}