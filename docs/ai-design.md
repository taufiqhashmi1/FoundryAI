# FoundryAI — AI Design

> **Status:** Ground-truth update — 2026-09-16
> **Implementation:** Spring AI gateway, agent-specific model routing, structured planning, structured specialist responses, CEO synthesis, and schema-validated structured output are implemented.

## 1. Purpose

FoundryAI uses LLMs for:

- interpretation,
- planning,
- domain reasoning,
- recommendations,
- structured specialist responses,
- final CEO synthesis,
- tool selection/calling where configured.

The LLM is not the authoritative component for:

- authorization,
- workflow state,
- persistence,
- deterministic arithmetic,
- policy enforcement,
- security controls,
- external side effects.

```text
LLM / Agent
    ↓
interpret / plan / reason / recommend
    ↓
Application
    ├── validate
    ├── persist
    ├── enforce policy
    ├── calculate
    └── execute tools
```

## 2. Current Stack

```text
Java 25
Spring Boot 4.1.1
Spring AI 2.0.1
Maven
MySQL 8.0.40
Hibernate ORM 7.4.5.Final
Groq
Spring AI OpenAI-compatible integration
```

Current development configuration uses the Groq API through Spring AI's OpenAI-compatible integration:

```properties
spring.ai.openai.api-key=${GROQ_API_KEY}
spring.ai.openai.base-url=https://api.groq.com/openai/v1
spring.ai.openai.chat.options.model=${GROQ_MODEL}
spring.ai.openai.chat.options.temperature=0.2
```

Agent-specific model settings are additionally resolved through `AgentConfig`.

## 3. AI Abstraction

Current implemented path:

```text
Concrete Agent
      ↓
AiModelGateway
      ↓
ModelRouter
      ↓
AgentSettings
      ↓
Spring AI ChatClient
      ↓
Groq
```

Implemented AI classes:

```text
AiModelGateway
AiModelGatewayImpl
ModelRouter
```

There is no separate `StructuredAiModelGateway` in the current implementation. The earlier `StructuredAiModelGateway` abstraction was consolidated into `AiModelGateway`.

## 4. Current AiModelGateway Contract

The gateway currently exposes three structured application operations:

```java
List<PlannedTask> generatePlan(
        AgentType agentType,
        String userPrompt
);

StructuredAgentResponse generateAgentResponse(
        AgentType agentType,
        String userPrompt,
        AgentContext context
);

CEORecommendationResponseDTO synthesize(
        AgentType agentType,
        String businessObjective,
        Map<UUID, AgentResult> specialistResults
);
```

The gateway hides Spring AI and provider-specific details from the rest of the application.

## 5. Model Routing

`ModelRouter` is the single component responsible for:

```text
AgentType → AgentConfig.AgentSettings
```

It supports:

```text
CEO
CFO
ENGINEERING
INFRASTRUCTURE
```

It validates that a model and system prompt are configured before returning settings.

## 6. Structured Planning

The CEO planner asks the model for a list of `PlannedTask` objects.

Each planned task contains:

```text
taskKey
agentType
objective
dependencies
```

The model uses human-readable task keys. `ExecutionPlanBuilder` later converts those keys and dependency keys into UUID-based runtime tasks.

```text
Business Objective
       ↓
AiModelGateway.generatePlan(...)
       ↓
List<PlannedTask>
       ↓
ExecutionPlanBuilder
       ↓
ExecutionPlan
```

`ExecutionPlanBuilder` rejects:

- empty plans,
- duplicate task keys,
- invalid task keys,
- blank objectives,
- missing agent types,
- CEO specialist workflow tasks,
- unknown dependency keys.

## 7. Structured Agent Responses

Specialist agents receive:

```text
business task
+
serialized AgentContext
```

and request a structured response:

```text
output
structuredData
assumptions
risks
```

The result is represented internally by `StructuredAgentResponse`, which is an agent-runtime object rather than a REST DTO.

## 8. Spring AI Structured Output

The current implementation does not manually parse raw model JSON into domain/DTO objects.

The gateway uses:

```java
.call()
.entity(
    SomeResponseClass.class,
    spec -> spec.validateSchema()
)
```

for structured model responses.

This is used for:

- `StructuredAgentResponse`,
- `List<PlannedTask>`,
- `CEORecommendationResponseDTO`.

`validateSchema()` delegates structured-output validation/self-correction to Spring AI rather than introducing a custom response validator or retry service.

The application still retains `ObjectMapper` in `AiModelGatewayImpl` because it is used for serializing `AgentContext` data before it is supplied to the model.

## 9. CEO Synthesis

CEO synthesis receives specialist results formatted by the gateway and asks the CEO model for:

```text
recommendation
keyFindings
assumptions
risks
nextSteps
```

The result is directly converted into:

```text
CEORecommendationResponseDTO
```

The grounding prompt explicitly tells the model not to invent unsupported facts and to preserve uncertainty from specialist results.

## 10. Tool Calling

Current path:

```text
Agent
  ↓
AiModelGatewayImpl
  ↓
AgentSettings.tools
  ↓
ToolRegistry
  ↓
ToolCallback
  ↓
Spring AI
  ↓
Tool implementation
  ↓
Model continuation
```

`ToolRegistry` resolves tool names into registered Spring AI `ToolCallback` objects.

It is a registry only; it is not an authorization engine.

## 11. Structured Output Failure Handling

The current design intentionally separates concerns:

```text
DTO
  → represents validated data shape

Spring AI
  → structured conversion + schema validation/self-correction

AiModelGateway
  → application-facing AI abstraction

GlobalExceptionHandler
  → HTTP error translation
```

No custom `AiResponseValidator`, `AiRetryHandler`, or DTO-owned retry mechanism is implemented.

## 12. Current Provider Boundary

The provider is currently Groq through Spring AI's OpenAI-compatible integration.

Provider-level structured-output support is not treated as an independent architectural requirement; the current implementation relies on Spring AI's `.entity(...validateSchema())` path.
