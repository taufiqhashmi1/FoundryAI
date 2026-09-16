package com.taufiqhashmi.foundryai.agents;

import com.taufiqhashmi.foundryai.ai.AiModelGateway;
import com.taufiqhashmi.foundryai.dtos.CEORecommendationResponseDTO;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CEOSynthesizerTest {

    @Test
    void shouldDelegateSynthesisToAiGateway() {

        AiModelGateway gateway =
                mock(AiModelGateway.class);

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

        CEORecommendationResponseDTO expectedRecommendation =
                CEORecommendationResponseDTO.builder()
                        .recommendation(
                                "Prioritize cloud cost reduction."
                        )
                        .keyFindings(
                                Map.of(
                                        "financialImpact",
                                        "Cloud costs can be reduced."
                                )
                        )
                        .assumptions(
                                List.of(
                                        "Current cloud costs remain stable."
                                )
                        )
                        .risks(
                                List.of(
                                        "Cost reduction may affect performance."
                                )
                        )
                        .nextSteps(
                                List.of(
                                        "Analyze the largest cloud cost drivers."
                                )
                        )
                        .build();

        when(
                gateway.synthesize(
                        AgentType.CEO,
                        "Reduce operating costs.",
                        specialistResults
                )
        ).thenReturn(
                expectedRecommendation
        );

        CEORecommendationResponseDTO result =
                synthesizer.synthesize(
                        "Reduce operating costs.",
                        specialistResults
                );

        assertEquals(
                expectedRecommendation,
                result
        );

        assertEquals(
                "Prioritize cloud cost reduction.",
                result.getRecommendation()
        );

        assertEquals(
                expectedRecommendation.getKeyFindings(),
                result.getKeyFindings()
        );

        assertEquals(
                expectedRecommendation.getAssumptions(),
                result.getAssumptions()
        );

        assertEquals(
                expectedRecommendation.getRisks(),
                result.getRisks()
        );

        assertEquals(
                expectedRecommendation.getNextSteps(),
                result.getNextSteps()
        );

        verify(gateway).synthesize(
                AgentType.CEO,
                "Reduce operating costs.",
                specialistResults
        );
    }


    @Test
    void shouldRejectBlankBusinessObjective() {

        AiModelGateway gateway =
                mock(AiModelGateway.class);

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

        AiModelGateway gateway =
                mock(AiModelGateway.class);

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

        AiModelGateway gateway =
                mock(AiModelGateway.class);

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

        AiModelGateway gateway =
                mock(AiModelGateway.class);

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