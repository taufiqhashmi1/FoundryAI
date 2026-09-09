package com.taufiqhashmi.foundryai.ai;

import com.taufiqhashmi.foundryai.agents.AgentConfig;
import com.taufiqhashmi.foundryai.agents.AgentType;
import com.taufiqhashmi.foundryai.tools.ToolRegistry;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class AiModelGatewayImpl implements AiModelGateway {

    private final ModelRouter modelRouter;
    private final ChatClient chatClient;
    private final ToolRegistry toolRegistry;

    public AiModelGatewayImpl(
            ModelRouter modelRouter,
            ChatClient.Builder chatClientBuilder,
            ToolRegistry toolRegistry
    ) {
        this.modelRouter = modelRouter;
        this.chatClient = chatClientBuilder.build();
        this.toolRegistry = toolRegistry;
    }

    @Override
    public String generate(
            AgentType agentType,
            String userPrompt
    ) {
        validateInput(agentType, userPrompt);

        AgentConfig.AgentSettings settings =
                modelRouter.resolve(agentType);

        List<ToolCallback> tools =
                resolveTools(settings);

        OpenAiChatOptions.Builder options =
                OpenAiChatOptions.builder()
                        .model(settings.getModel())
                        .temperature(settings.getTemperature())
                        .maxTokens(settings.getMaxTokens());

        if (settings.getIncludeReasoning() != null) {
            options.extraBody(
                    Map.of(
                            "include_reasoning",
                            settings.getIncludeReasoning()
                    )
            );
        }

        return chatClient.prompt()
                .system(settings.getSystemPrompt())
                .user(userPrompt)
                .options(options)
                .tools(tools)
                .call()
                .content();
    }

    private List<ToolCallback> resolveTools(
            AgentConfig.AgentSettings settings
    ) {
        if (settings.getTools() == null ||
                settings.getTools().isEmpty()) {

            return List.of();
        }

        return settings.getTools()
                .stream()
                .map(toolRegistry::getTool)
                .toList();
    }

    private void validateInput(
            AgentType agentType,
            String userPrompt
    ) {
        if (agentType == null) {
            throw new IllegalArgumentException(
                    "Agent type cannot be null"
            );
        }

        if (userPrompt == null || userPrompt.isBlank()) {
            throw new IllegalArgumentException(
                    "User prompt cannot be null or blank"
            );
        }
    }
}