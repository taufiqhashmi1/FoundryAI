package com.taufiqhashmi.foundryai.agents;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "foundryai.agents")
public class AgentConfig {

    private AgentSettings ceo;
    private AgentSettings cfo;
    private AgentSettings engineering;
    private AgentSettings infrastructure;

    @Getter
    @Setter
    public static class AgentSettings {

        private String model;

        private String systemPrompt;

        private Double temperature;

        private List<String> tools;

        private Integer maxTokens;

        private Boolean includeReasoning;
    }
}