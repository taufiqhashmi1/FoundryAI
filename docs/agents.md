# FoundryAI — Agent Architecture

> **Status:** Updated 2026-09-08

## 1. Purpose

Agents are specialized reasoning components operating within explicit application boundaries. An agent is not an unrestricted autonomous process.

The MVP contains exactly four roles:

1. CEO / Orchestrator
2. CFO
3. Engineering
4. Infrastructure

Security, Operations, and other specialists remain future extensions.

## 2. Core Contract

```java
public interface Agent {
    AgentType getType();
    AgentResult execute(AgentTask task);
}
```

Current runtime objects:

```text
Agent
AgentType
AgentTask
AgentContext
AgentResult
AgentResultStatus
AgentConfig
AgentRegistry
CEOPlanner
```

These are runtime/application concepts, not JPA entities or REST DTOs.

## 3. Runtime Lifecycle

```text
WorkflowEngine
    ↓
AgentTask
    ↓
AgentContext
    ↓
AgentRegistry
    ↓
Concrete Agent
    ↓
AiModelGateway
    ↓
ModelRouter
    ↓
Spring AI / Groq
    ↓
AgentResult
    ↓
WorkflowEngine
```

Tool-enabled execution:

```text
Agent
 ↓
AiModelGateway
 ↓
configured tool identifiers
 ↓
ToolRegistry
 ↓
ToolCallback
 ↓
deterministic tool
 ↓
model continuation
 ↓
AgentResult
```

## 4. CEO / Orchestrator

`CEOPlanner` converts a business objective into a structured `ExecutionPlan`.

```text
Business Objective
       ↓
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

The planner currently creates specialist tasks for CFO, Engineering, and Infrastructure.

The CEO must not directly call concrete specialist agents.

Core boundary:

```text
CEO decides WHAT.
Workflow decides WHEN / WHETHER.
Agent decides HOW.
Tool decides HOW a system action is performed.
```

### Synthesis

Final synthesis belongs to the CEO/orchestration layer. The planner should not manufacture a final CFO synthesis task merely because the model can represent one.

Desired flow:

```text
specialist results
 ↓
CEO synthesis
 ↓
final business outcome
```

## 5. CFO Agent

Responsible for financial analysis, cost/risk analysis, and financial implications.

Authoritative arithmetic belongs to deterministic application code.

Current implemented tool example:

```text
financial-calculator
```

## 6. Engineering Agent

Responsible for software architecture, technical feasibility, implementation planning, and future controlled repository operations.

The current implementation is intentionally thin and provider-independent.

## 7. Infrastructure Agent

Responsible for infrastructure architecture, deployment planning, scalability, reliability, and cost analysis.

The current implementation is intentionally thin.

Production deployment and destructive infrastructure changes remain future policy/approval-controlled capabilities.

## 8. Concrete Agents

Exact classes:

```text
CEOAgent
CFOAgent
EngineeringAgent
InfrastructureAgent
```

Concrete agents should not contain workflow orchestration, dependency resolution, provider routing, persistence orchestration, or future memory infrastructure.

No abstract base agent is currently necessary.

## 9. Agent-to-Agent Collaboration

Agents do not freely chat.

```text
Agent A
  ↓
AgentResult
  ↓
WorkflowEngine
  ↓
TaskContextBuilder
  ↓
AgentTask.context
  ↓
Agent B
```

The current context propagation is tested.

## 10. AgentContext

Current:

```java
public class AgentContext {
    private Map<String, Object> data;
}
```

It is the current working-context extension point for future CoALA-compatible memory.

Dependency results currently appear under:

```text
dependencyResults
```

with value:

```text
Map<UUID, AgentResult>
```

This is appropriate for the workflow runtime. A future LLM-facing projection should convert this into readable/labeled context instead of exposing raw UUID maps directly.

## 11. AgentResult

Current:

```java
public class AgentResult {
    private AgentType agentType;
    private AgentResultStatus status;
    private String output;
    private String error;
    private Map<String, Object> metadata;
}
```

```java
public enum AgentResultStatus {
    SUCCESS,
    FAILURE
}
```

The previous boolean-success representation was replaced by an explicit status to reduce invalid state combinations.

## 12. AgentConfig

Configuration prefix:

```text
foundryai.agents
```

Per-agent settings:

```text
model
systemPrompt
temperature
tools
includeReasoning
```

Current logical agents:

```text
ceo
cfo
engineering
infrastructure
```

Tool values are identifiers, not tool objects.

`includeReasoning=false` is currently used for Groq GPT-OSS tool-call compatibility.

## 13. AgentRegistry

Maps:

```text
AgentType → Agent
```

It collects Spring-managed agents and rejects duplicate registrations.

Workflow code resolves agents through the registry rather than depending directly on concrete classes.

## 14. Failure Handling

Current workflow behavior converts runtime dispatch failures into failed `AgentResult` values.

Future production behavior should add bounded retries, timeouts, failure classification, and controlled re-planning.

An agent must never hide a model/tool failure behind a fabricated successful result.

## 15. Testing

Verified:

- real CEO model execution,
- real CEO tool calling,
- structured CEO planning,
- workflow dependency behavior,
- task-context construction,
- dependency-result propagation into a dependent agent.

The context integration test verifies:

```text
CFO result
 ↓
TaskContextBuilder
 ↓
Engineering AgentTask.context
 ↓
Engineering receives CFO result
```

## 16. Fundamental Rule

> An agent may reason about an action, but the platform decides whether the action is permitted and how it is executed.
