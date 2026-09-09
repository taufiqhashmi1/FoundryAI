# FoundryAI — AI Design

> **Status:** Updated 2026-09-08  
> **Implementation:** AI gateway, model routing, structured planning, and Groq integration are implemented.

## 1. Purpose

FoundryAI uses LLMs for interpretation, planning, reasoning, delegation, tool selection, synthesis, and recommendations.

The LLM is not authoritative for authorization, financial arithmetic, policy, persistence, workflow state, security controls, or external side effects.

```text
LLM / Agent
  ↓
reason / plan / select
  ↓
Application
  ├── validate
  ├── authorize
  ├── calculate
  ├── persist
  ├── enforce policy
  └── execute tools
```

## 2. Current Stack

```text
Java 25
Spring Boot 4.1.1
Spring AI 2.0.1
Maven
Groq
Spring AI OpenAI-compatible integration
```

```properties
spring.ai.openai.api-key=${GROQ_API_KEY}
spring.ai.openai.base-url=https://api.groq.com/openai/v1
spring.ai.openai.chat.options.model=${GROQ_MODEL}
spring.ai.openai.chat.options.temperature=0.2
```

Agent-specific model configuration remains supported.

## 3. Implemented AI Abstraction

```text
Agent
  ↓
AgentConfig
  ↓
AiModelGateway
  ↓
ModelRouter
  ↓
Spring AI ChatClient
  ↓
Groq
```

Implemented:

```text
AiModelGateway
AiModelGatewayImpl
ModelRouter
```

`ModelRouter` is the single component responsible for `AgentType → AgentSettings` mapping.

## 4. Structured Planning

Implemented:

```text
StructuredAiModelGateway
StructuredAiModelGatewayImpl
CEOPlanner
PlannedTask
ExecutionPlan
ExecutionPlanBuilder
```

Contract:

```java
List<PlannedTask> generatePlan(
    AgentType agentType,
    String userPrompt
);
```

Flow:

```text
CEOPlanner
 ↓
StructuredAiModelGateway
 ↓
PlannedTask[]
 ↓
ExecutionPlanBuilder
 ↓
ExecutionPlan
```

`PlannedTask` contains:

```text
taskKey
agentType
objective
dependencies
```

Dependencies are model-friendly task keys and become UUID references at runtime.

## 5. CEO Planning Boundary

The planner creates specialist execution work.

It does not execute tasks.

Canonical flow:

```text
CEO
 ↓
specialist plan
 ↓
WorkflowEngine
 ↓
specialist results
 ↓
CEO synthesis
```

Final synthesis should not be assigned to CFO as an incidental planning artifact.

## 6. Tool Calling

Implemented path:

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

The model never receives arbitrary internal-service access.

## 7. Tool Selection

Agent configuration contains declarative tool names.

```text
AgentConfig
 ↓
allowed tool names
 ↓
ToolRegistry
 ↓
ToolCallback
```

No separate `AgentToolResolver` is required for the current design.

`ToolRegistry` is a registry, not an authorization engine.

## 8. Groq GPT-OSS Compatibility

The gateway supports the Groq-compatible extra body:

```java
options.extraBody(
    Map.of(
        "include_reasoning",
        settings.getIncludeReasoning()
    )
);
```

Current agent configuration uses:

```properties
foundryai.agents.<agent>.include-reasoning=false
```

This addresses the GPT-OSS `reasoning_content` tool-call compatibility issue encountered during integration testing.

## 9. Structured Outputs

Current structured planning converts model output into:

```text
List<PlannedTask>
```

`AgentResult` remains intentionally small.

Future richer artifacts may contain:

```text
summary
assumptions
evidence
metrics
findings
recommendations
proposedActions
risks
confidence
```

Do not introduce a large result hierarchy until concrete runtime requirements justify it.

## 10. Context Engineering

`TaskContextBuilder` currently combines existing task context and dependency results.

Dependency results are stored as:

```text
context.data["dependencyResults"]
```

with:

```text
Map<UUID, AgentResult>
```

Future LLM-facing context should project only relevant information.

## 11. CoALA and Memory

CoALA is an architectural framework, not a dependency.

Relevant categories:

```text
Working
Episodic
Semantic
Procedural
```

Current decision:

> Design for memory now; implement memory later.

Do not add:

```text
memory/
vector DB
embeddings
memory repositories
memory consolidator
```

yet.

## 12. RAG

RAG is future infrastructure.

Good candidates include architecture documents, company policies, procedures, security standards, and product documentation.

RAG must not become authoritative for current financial balances, transactions, workflow state, authorization, or approvals.

## 13. Reasoning Artifacts

Do not expose private chain-of-thought.

Prefer:

```text
summary
assumptions
evidence
findings
recommendations
risks
proposedActions
confidence
```

## 14. Hallucination Controls

Use:

1. deterministic tools,
2. structured outputs,
3. schema validation,
4. evidence where available,
5. policy checks,
6. evaluation tests,
7. human approval for high-impact actions.

## 15. Future AI Capabilities

Potential extensions:

- bounded parallel agent execution,
- model fallback,
- evaluation pipelines,
- semantic caching,
- retrieval,
- multimodal inputs,
- specialized reasoning models,
- event-driven workers.

These must extend the existing boundaries rather than replace them.
