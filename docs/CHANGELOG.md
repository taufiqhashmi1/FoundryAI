# FoundryAI Documentation Changelog

## 2026-09-08

This refresh reconciles the documentation with the implementation reached after the previous 2026-09-05 documentation baseline.

### Newly implemented since the old baseline

```text
AiModelGateway
AiModelGatewayImpl
ModelRouter

CEOAgent
CFOAgent
EngineeringAgent
InfrastructureAgent

StructuredAiModelGateway
StructuredAiModelGatewayImpl
CEOPlanner

PlannedTask
ExecutionPlan
ExecutionPlanBuilder

ToolRegistry
ToolConfiguration
FinancialCalculatorTool

TaskDependencyResolver
WorkflowTaskDispatcher
TaskContextBuilder
WorkflowEngine
WorkflowResult
WorkflowTaskStatus
```

### Agent result model

`AgentResult` now uses:

```text
AgentResultStatus.SUCCESS
AgentResultStatus.FAILURE
```

instead of the earlier mutable boolean-success representation.

### AI configuration

`AgentConfig` now includes:

```text
model
systemPrompt
temperature
tools
includeReasoning
```

`includeReasoning=false` is used for Groq GPT-OSS tool-call compatibility.

### Tool architecture

The implemented MVP uses:

```text
AgentConfig
 ↓
ToolRegistry
 ↓
ToolCallback
```

A separate `AgentToolResolver` is not required.

### Workflow architecture

The runtime now supports:

- task validation,
- dependency validation,
- runnable-task detection,
- dependency blocking,
- dispatch through `AgentRegistry`,
- result collection,
- dependency-result context propagation,
- cycle/no-progress detection,
- runtime failure conversion.

The engine is currently sequential.

### Context propagation

Dependent tasks receive:

```text
AgentTask.context.data["dependencyResults"]
```

with:

```text
Map<UUID, AgentResult>
```

### CEO planning correction

The CEO planner produces specialist execution work.

Final synthesis belongs to the CEO/orchestration layer and should not be represented as an arbitrary CFO synthesis task.

### Still future

```text
CEO final synthesis
RequestService
WorkflowService
runtime ↔ persistence integration
REST controllers
policy engine
approval system
audit persistence
authentication/authorization
retries/timeouts
parallel execution
production integrations
CoALA memory
production hardening
```
