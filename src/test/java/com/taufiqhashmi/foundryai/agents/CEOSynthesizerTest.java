package com.taufiqhashmi.foundryai.agents;

import com.taufiqhashmi.foundryai.ai.StructuredAiModelGateway;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CEOSynthesizerTest {

    @Test
    void shouldDelegateSynthesisToStructuredAiGateway() {

        StructuredAiModelGateway gateway =
                mock(StructuredAiModelGateway.class);

        CEOSynthesizer synthesizer =
                new CEOSynthesizer(gateway);

        UUID taskId =
                UUID.randomUUID();

        AgentResult specialistResult =
                AgentResult.builder()
                        .agentType(AgentType.CFO)
                        .status(AgentResultStatus.SUCCESS)
                        .output(
                                "Cloud costs should be reduced."
                        )
                        .build();

        Map<UUID, AgentResult> specialistResults =
                Map.of(
                        taskId,
                        specialistResult
                );

        when(
                gateway.synthesize(
                        AgentType.CEO,
                        "Reduce operating costs.",
                        specialistResults
                )
        ).thenReturn(
                "Prioritize cloud cost reduction."
        );

        String result =
                synthesizer.synthesize(
                        "Reduce operating costs.",
                        specialistResults
                );

        assertEquals(
                "Prioritize cloud cost reduction.",
                result
        );

        verify(gateway).synthesize(
                AgentType.CEO,
                "Reduce operating costs.",
                specialistResults
        );
    }

    @Test
    void shouldRejectBlankBusinessObjective() {

        StructuredAiModelGateway gateway =
                mock(StructuredAiModelGateway.class);

        CEOSynthesizer synthesizer =
                new CEOSynthesizer(gateway);

        UUID taskId =
                UUID.randomUUID();

        AgentResult specialistResult =
                AgentResult.builder()
                        .agentType(AgentType.CFO)
                        .status(AgentResultStatus.SUCCESS)
                        .output(
                                "Financial analysis complete."
                        )
                        .build();

        Map<UUID, AgentResult> specialistResults =
                Map.of(
                        taskId,
                        specialistResult
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> synthesizer.synthesize(
                        "",
                        specialistResults
                )
        );
    }

    @Test
    void shouldRejectNullBusinessObjective() {

        StructuredAiModelGateway gateway =
                mock(StructuredAiModelGateway.class);

        CEOSynthesizer synthesizer =
                new CEOSynthesizer(gateway);

        UUID taskId =
                UUID.randomUUID();

        AgentResult specialistResult =
                AgentResult.builder()
                        .agentType(AgentType.CFO)
                        .status(AgentResultStatus.SUCCESS)
                        .output(
                                "Financial analysis complete."
                        )
                        .build();

        Map<UUID, AgentResult> specialistResults =
                Map.of(
                        taskId,
                        specialistResult
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> synthesizer.synthesize(
                        null,
                        specialistResults
                )
        );
    }

    @Test
    void shouldRejectEmptySpecialistResults() {

        StructuredAiModelGateway gateway =
                mock(StructuredAiModelGateway.class);

        CEOSynthesizer synthesizer =
                new CEOSynthesizer(gateway);

        assertThrows(
                IllegalArgumentException.class,
                () -> synthesizer.synthesize(
                        "Reduce operating costs.",
                        Map.of()
                )
        );
    }

    @Test
    void shouldRejectNullSpecialistResults() {

        StructuredAiModelGateway gateway =
                mock(StructuredAiModelGateway.class);

        CEOSynthesizer synthesizer =
                new CEOSynthesizer(gateway);

        assertThrows(
                IllegalArgumentException.class,
                () -> synthesizer.synthesize(
                        "Reduce operating costs.",
                        null
                )
        );
    }
}