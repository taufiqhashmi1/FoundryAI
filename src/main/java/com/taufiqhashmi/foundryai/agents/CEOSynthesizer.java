package com.taufiqhashmi.foundryai.agents;

import com.taufiqhashmi.foundryai.ai.AiModelGateway;
import com.taufiqhashmi.foundryai.dtos.CEORecommendationResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CEOSynthesizer {

    private final AiModelGateway aiModelGateway;

    public CEORecommendationResponseDTO synthesize(
            String businessObjective,
            Map<UUID, AgentResult> specialistResults
    ) {

        if (businessObjective == null ||
                businessObjective.isBlank()) {

            throw new IllegalArgumentException(
                    "Business objective cannot be null or blank"
            );
        }

        if (specialistResults == null ||
                specialistResults.isEmpty()) {

            throw new IllegalArgumentException(
                    "Specialist results cannot be null or empty"
            );
        }

        return aiModelGateway.synthesize(
                AgentType.CEO,
                businessObjective,
                specialistResults
        );
    }
}