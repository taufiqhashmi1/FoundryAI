package com.taufiqhashmi.foundryai.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CEORecommendationResponseDTO {

    private String recommendation;

    private Map<String, Object> keyFindings;

    private List<String> assumptions;

    private List<String> risks;

    private List<String> nextSteps;
}