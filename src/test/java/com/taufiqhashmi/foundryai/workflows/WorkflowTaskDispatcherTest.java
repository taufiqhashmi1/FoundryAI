package com.taufiqhashmi.foundryai.workflows;

import com.taufiqhashmi.foundryai.agents.Agent;
import com.taufiqhashmi.foundryai.agents.AgentRegistry;
import com.taufiqhashmi.foundryai.agents.AgentResult;
import com.taufiqhashmi.foundryai.agents.AgentResultStatus;
import com.taufiqhashmi.foundryai.agents.AgentTask;
import com.taufiqhashmi.foundryai.agents.AgentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WorkflowTaskDispatcherTest {

    private AgentRegistry agentRegistry;

    private WorkflowTaskDispatcher dispatcher;

    private Agent agent;

    @BeforeEach
    void setUp() {

        agent = mock(Agent.class);

        agentRegistry = mock(AgentRegistry.class);

        dispatcher = new WorkflowTaskDispatcher(
                agentRegistry
        );
    }

    @Test
    void shouldDispatchTaskToAgentFromRegistry() {

        AgentTask task = AgentTask.builder()
                .taskId(java.util.UUID.randomUUID())
                .agentType(AgentType.CFO)
                .objective("Analyze quarterly expenses")
                .build();

        AgentResult expectedResult =
                AgentResult.builder()
                        .agentType(AgentType.CFO)
                        .status(AgentResultStatus.SUCCESS)
                        .output("Expense analysis completed")
                        .build();

        when(agentRegistry.getAgent(AgentType.CFO))
                .thenReturn(agent);

        when(agent.execute(task))
                .thenReturn(expectedResult);

        AgentResult actualResult =
                dispatcher.dispatch(task);

        assertEquals(
                expectedResult,
                actualResult
        );

        verify(agentRegistry)
                .getAgent(AgentType.CFO);

        verify(agent)
                .execute(task);
    }

    @Test
    void shouldRejectNullTask() {

        assertThrows(
                IllegalArgumentException.class,
                () -> dispatcher.dispatch(null)
        );
    }

    @Test
    void shouldRejectTaskWithoutAgentType() {

        AgentTask task = AgentTask.builder()
                .taskId(java.util.UUID.randomUUID())
                .objective("Missing agent type")
                .build();

        assertThrows(
                IllegalArgumentException.class,
                () -> dispatcher.dispatch(task)
        );
    }
}