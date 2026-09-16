package com.taufiqhashmi.foundryai.agents;

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
public class StructuredAgentResponse {

    private String output;

    private Map<String, Object> structuredData;

    private List<String> assumptions;

    private List<String> risks;
}