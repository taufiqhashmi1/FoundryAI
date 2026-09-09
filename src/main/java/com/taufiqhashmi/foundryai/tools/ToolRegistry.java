package com.taufiqhashmi.foundryai.tools;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ToolRegistry {

    private final Map<String, ToolCallback> tools;

    public ToolRegistry(
            ToolCallbackProvider toolCallbackProvider
    ) {
        this.tools = new HashMap<>();

        for (ToolCallback toolCallback :
                toolCallbackProvider.getToolCallbacks()) {

            String toolName = toolCallback
                    .getToolDefinition()
                    .name();

            if (tools.containsKey(toolName)) {
                throw new IllegalStateException(
                        "Multiple tools registered with name: " + toolName
                );
            }

            tools.put(toolName, toolCallback);
        }
    }

    public ToolCallback getTool(String toolName) {

        if (toolName == null || toolName.isBlank()) {
            throw new IllegalArgumentException(
                    "Tool name cannot be null or blank"
            );
        }

        ToolCallback tool = tools.get(toolName);

        if (tool == null) {
            throw new IllegalArgumentException(
                    "No tool registered with name: " + toolName
            );
        }

        return tool;
    }
}