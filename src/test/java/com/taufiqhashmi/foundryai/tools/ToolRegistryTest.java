package com.taufiqhashmi.foundryai.tools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.definition.ToolDefinition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ToolRegistryTest {

    private ToolCallback financialCalculatorTool;

    private ToolRegistry toolRegistry;

    @BeforeEach
    void setUp() {

        financialCalculatorTool = mock(ToolCallback.class);

        ToolDefinition toolDefinition =
                mock(ToolDefinition.class);

        when(toolDefinition.name())
                .thenReturn("financial-calculator");

        when(financialCalculatorTool.getToolDefinition())
                .thenReturn(toolDefinition);

        ToolCallbackProvider toolCallbackProvider =
                mock(ToolCallbackProvider.class);

        when(toolCallbackProvider.getToolCallbacks())
                .thenReturn(
                        new ToolCallback[]{
                                financialCalculatorTool
                        }
                );

        toolRegistry = new ToolRegistry(
                toolCallbackProvider
        );
    }

    @Test
    void shouldResolveRegisteredToolByName() {

        ToolCallback result =
                toolRegistry.getTool("financial-calculator");

        assertEquals(
                financialCalculatorTool,
                result
        );
    }

    @Test
    void shouldRejectUnknownTool() {

        assertThrows(
                IllegalArgumentException.class,
                () -> toolRegistry.getTool("unknown-tool")
        );
    }
}