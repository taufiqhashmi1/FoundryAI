package com.taufiqhashmi.foundryai.ai;

import com.taufiqhashmi.foundryai.agents.AgentConfig;
import com.taufiqhashmi.foundryai.agents.AgentResult;
import com.taufiqhashmi.foundryai.agents.AgentType;
import com.taufiqhashmi.foundryai.workflows.PlannedTask;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class StructuredAiModelGatewayImpl
                implements StructuredAiModelGateway {

        private final ModelRouter modelRouter;
        private final ChatClient chatClient;

        public StructuredAiModelGatewayImpl(
                        ModelRouter modelRouter,
                        ChatClient.Builder chatClientBuilder) {
                this.modelRouter = modelRouter;
                this.chatClient = chatClientBuilder.build();
        }

        @Override
        public List<PlannedTask> generatePlan(
                        AgentType agentType,
                        String userPrompt) {
                validatePlanInput(
                                agentType,
                                userPrompt);

                AgentConfig.AgentSettings settings = modelRouter.resolve(agentType);

                return chatClient.prompt()
                                .system(settings.getSystemPrompt())
                                .user("""
                                                Create an execution plan for the following
                                                business objective.

                                                Available specialist agents:
                                                - CFO
                                                - ENGINEERING
                                                - INFRASTRUCTURE

                                                Requirements:
                                                - Create only specialist workflow tasks.
                                                - Do not create a CEO task.
                                                - The CEO will perform final synthesis.
                                                - Only assign a specialist when that specialist's
                                                domain expertise is materially required to answer
                                                the business objective.
                                                - Do not create tasks merely to obtain additional
                                                perspectives.
                                                - If the objective can be answered adequately by
                                                fewer specialists, do not create unnecessary
                                                specialist tasks.
                                                - Do not require unsupported estimates or invented
                                                business data.
                                                - Every task must contain:
                                                - taskKey
                                                - agentType
                                                - objective
                                                - dependencies
                                                - taskKey values must be unique.
                                                - Dependencies must reference taskKeys
                                                in this plan.
                                                - Use an empty dependency list when
                                                no dependency exists.
                                                - Create only tasks necessary to
                                                accomplish the objective.

                                                Business objective:
                                                %s
                                                """.formatted(userPrompt))
                                .options(
                                                OpenAiChatOptions.builder()
                                                                .model(settings.getModel())
                                                                .temperature(settings.getTemperature())
                                                                .maxTokens(settings.getMaxTokens()))
                                .call()
                                .entity(
                                                new ParameterizedTypeReference<List<PlannedTask>>() {
                                                });
        }

        @Override
        public String synthesize(
                        AgentType agentType,
                        String businessObjective,
                        Map<UUID, AgentResult> specialistResults) {
                validateSynthesisInput(
                                agentType,
                                businessObjective,
                                specialistResults);

                AgentConfig.AgentSettings settings = modelRouter.resolve(agentType);

                String specialistContext = formatSpecialistResults(specialistResults);

                return chatClient.prompt()
                                .system(settings.getSystemPrompt())
                                .user("""
                                                Synthesize the specialist results into a
                                                final executive recommendation.

                                                Business objective:
                                                %s

                                                Specialist results:
                                                %s

                                                IMPORTANT GROUNDING RULES:
                                                - Treat specialist outputs as the only source
                                                  of factual findings.
                                                - Do not invent financial, engineering,
                                                  infrastructure, operational, customer,
                                                  revenue, cost, or performance data.
                                                - Do not turn an assumption into a fact.
                                                - Preserve explicit uncertainty from
                                                  specialist results.
                                                - If important information is missing,
                                                  say so.
                                                - If a recommendation depends on assumptions,
                                                  identify those assumptions explicitly.
                                                - Do not claim a tool was used unless the
                                                  specialist result explicitly contains a
                                                  tool-derived result.
                                                - Do not perform important financial
                                                  calculations mentally when the required
                                                  inputs are uncertain or unsupported.
                                                - If specialist results conflict,
                                                  explicitly identify the conflict.

                                                Your response should include:
                                                - executive recommendation
                                                - supporting findings
                                                - key assumptions and unknowns
                                                - key risks and trade-offs
                                                - recommended next steps

                                                Keep the recommendation concise and
                                                decision-oriented.

                                                Do not invent findings that are not supported
                                                by the specialist results.
                                                """.formatted(
                                                businessObjective,
                                                specialistContext))
                                .options(
                                                OpenAiChatOptions.builder()
                                                                .model(settings.getModel())
                                                                .temperature(settings.getTemperature())
                                                                .maxTokens(settings.getMaxTokens()))
                                .call()
                                .content();
        }

        private void validatePlanInput(
                        AgentType agentType,
                        String userPrompt) {
                if (agentType == null) {
                        throw new IllegalArgumentException(
                                        "Agent type cannot be null");
                }

                if (userPrompt == null || userPrompt.isBlank()) {
                        throw new IllegalArgumentException(
                                        "User prompt cannot be null or blank");
                }
        }

        private void validateSynthesisInput(
                        AgentType agentType,
                        String businessObjective,
                        Map<UUID, AgentResult> specialistResults) {
                if (agentType == null) {
                        throw new IllegalArgumentException(
                                        "Agent type cannot be null");
                }

                if (businessObjective == null ||
                                businessObjective.isBlank()) {
                        throw new IllegalArgumentException(
                                        "Business objective cannot be null or blank");
                }

                if (specialistResults == null ||
                                specialistResults.isEmpty()) {
                        throw new IllegalArgumentException(
                                        "Specialist results cannot be null or empty");
                }
        }

        private String formatSpecialistResults(
                        Map<UUID, AgentResult> specialistResults) {
                StringBuilder builder = new StringBuilder();

                specialistResults.forEach((taskId, result) -> {

                        builder.append("Task ID: ")
                                        .append(taskId)
                                        .append("\n");

                        builder.append("Agent: ")
                                        .append(result.getAgentType())
                                        .append("\n");

                        builder.append("Status: ")
                                        .append(result.getStatus())
                                        .append("\n");

                        if (result.getOutput() != null) {
                                builder.append("Output: ")
                                                .append(result.getOutput())
                                                .append("\n");
                        }

                        if (result.getError() != null) {
                                builder.append("Error: ")
                                                .append(result.getError())
                                                .append("\n");
                        }

                        builder.append("\n");
                });

                return builder.toString();
        }
}