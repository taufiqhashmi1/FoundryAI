package com.taufiqhashmi.foundryai.tools;

import com.taufiqhashmi.foundryai.tools.implementations.FinancialCalculatorTool;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolConfiguration {

    @Bean
    public ToolCallbackProvider toolCallbackProvider(
            FinancialCalculatorTool financialCalculatorTool
    ) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(financialCalculatorTool)
                .build();
    }
}