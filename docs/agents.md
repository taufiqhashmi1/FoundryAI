# FoundryAI — Agent Architecture

> **Status:** Ground-truth update — 2026-09-16
> **Scope:** Current implemented MVP runtime, including planning, specialist execution, CEO synthesis, tool usage, and workflow context propagation.

## 1. Purpose

FoundryAI uses specialized agents as bounded reasoning components inside an application-controlled workflow. Agents do not directly orchestrate other agents and do not own persistence, authorization, workflow state, or external side effects.

The current MVP contains exactly four agent types:

1. CEO / Orchestrator
2. CFO
3. Engineering
4. Infrastructure

Security, Operations, and other specialists are not implemented.

## 2. Core Agent Contract

```java
public interface Agent {
    AgentType getType();
    AgentResult execute(AgentTask task);
}
```

Runtime agent objects are application/runtime concepts. They are not JPA entities and are not REST DTOs.

## 3. Agent Types

```java
public enum AgentType {
    CEO,
    CFO,
    ENGINEERING,
    INFRASTRUCTURE
}
```

## 4. Runtime Objects

The current runtime contains:

```text
Agent
AgentType
AgentTask
AgentContext
AgentResult
AgentResultStatus
StructuredAgentResponse
AgentConfig
AgentRegistry
CEOPlanner
CEOSynthesizer
```

### AgentTask

`AgentTask` is the transient instruction passed through the workflow runtime.

Conceptually:

```text
taskId
agentType
objective
dependencies
context
```

`dependencies` are workflow task UUIDs. The workflow engine resolves when those dependencies are runnable; the agent does not perform dependency orchestration.

### AgentContext

`AgentContext` is the runtime working context:

```text
Map<String, Object> data
```

The current workflow context uses:

```text
context.data["dependencyResults"]
```

where the value is a map keyed by dependency task UUID. Each dependency result is currently represented as a serializable map containing selected `AgentResult` fields:

```text
agentType
output
structuredData
assumptions
risks
```

The current `TaskContextBuilder` does not preserve an existing `AgentTask.context`; it builds a new `AgentContext`.

### AgentResult

`AgentResult` is the standardized result returned by an agent:

```text
agentType
status
output
structuredData
assumptions
risks
error
metadata
```

`AgentResultStatus` is:

```text
SUCCESS
FAILURE
```

## 5. Agent Lifecycle

```text
WorkflowEngine
    ↓
AgentTask
    ↓
TaskContextBuilder
    ↓
WorkflowTaskDispatcher
    ↓
AgentRegistry
    ↓
Concrete Agent
    ↓
AiModelGateway
    ↓
ModelRouter
    ↓
Spring AI ChatClient
    ↓
Groq
    ↓
StructuredAgentResponse
    ↓
AgentResult
    ↓
WorkflowEngine
```

The concrete agents are intentionally thin adapters. They validate the task, invoke `AiModelGateway`, convert the structured response into `AgentResult`, and return failure results when model execution fails.

## 6. CEO / Orchestrator

The CEO has two distinct responsibilities in the current implementation:

### Planning

`CEOPlanner` converts a business objective into a structured `ExecutionPlan`.

```text
Business Objective
      ↓
CEOPlanner
      ↓
AiModelGateway.generatePlan(...)
      ↓
PlannedTask[]
      ↓
ExecutionPlanBuilder
      ↓
ExecutionPlan
```

The planner creates specialist tasks only. It must not create a CEO task for final synthesis.

### Final synthesis

`CEOSynthesizer` receives specialist `AgentResult` objects and calls the CEO-configured model through `AiModelGateway.synthesize(...)`.

```text
specialist AgentResult map
        ↓
CEOSynthesizer
        ↓
AiModelGateway.synthesize(...)
        ↓
CEORecommendationResponseDTO
```

The CEO therefore decides the business-level plan and produces the final recommendation, while the workflow engine controls execution.

## 7. Responsibility Boundary

```text
CEO      → WHAT work is needed and final synthesis
Workflow → WHEN / WHETHER a task may run
Agent    → HOW to reason about an assigned task
Tool     → HOW a deterministic application operation is performed
```

The CEO does not call:

```java
cfoAgent.execute(...)
engineeringAgent.execute(...)
```

directly.

## 8. CFO Agent

Current responsibility:

- financial reasoning,
- cost/risk analysis,
- financial implications,
- structured financial findings.

The current deterministic tool is `financial-calculator`.

The LLM is not considered authoritative for arithmetic when deterministic application code can perform the calculation.

## 9. Engineering Agent

Current responsibility:

- software/technical reasoning,
- architecture analysis,
- technical feasibility,
- implementation/security planning.

The current implementation is intentionally thin and provider-independent.

Repository mutation, pull-request creation, and other external engineering actions are not currently implemented.

## 10. Infrastructure Agent

Current responsibility:

- infrastructure reasoning,
- deployment planning,
- scalability/reliability analysis,
- infrastructure cost analysis.

Production infrastructure mutation is not currently implemented.

## 11. Concrete Agent Classes

```text
agents/implementations/
├── CEOAgent.java
├── CFOAgent.java
├── EngineeringAgent.java
└── InfrastructureAgent.java
```

No abstract base agent is required in the current MVP.

## 12. Agent Registry

`AgentRegistry` builds an `EnumMap<AgentType, Agent>` from Spring-managed `Agent` implementations.

It rejects duplicate registrations and fails when a requested agent type is missing.

This keeps `WorkflowTaskDispatcher` independent of concrete agent classes.

## 13. Agent Configuration

`AgentConfig` uses explicit properties:

```text
ceo
cfo
engineering
infrastructure
```

Each `AgentSettings` contains:

```text
model
systemPrompt
temperature
maxTokens
tools
includeReasoning
```

The explicit field structure is intentional; the earlier enum/map-based configuration approach was discarded because binding was unreliable in the current setup.

## 14. Collaboration Model

Agents do not freely chat.

Current collaboration is:

```text
Agent A
   ↓
AgentResult
   ↓
WorkflowEngine
   ↓
TaskContextBuilder
   ↓
Dependent AgentTask.context
   ↓
Agent B
```

This makes dependencies explicit, testable, and bounded.

## 15. Memory

`AgentContext` is the extension point for working memory.

CoALA-style semantic, episodic, procedural, and persistent memory infrastructure is not implemented. No vector database, embedding layer, memory repository, or memory-consolidation subsystem is part of the MVP.
