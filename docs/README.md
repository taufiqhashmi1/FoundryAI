# FoundryAI Documentation

> **Documentation updated:** 2026-09-08

## Document Set

```text
README.md
vision.md
requirements.md
architecture.md
agents.md
ai-design.md
tools.md
workflows.md
data-model.md
mvp-plan.md
progress.md
apis.md
security.md
governance.md
evaluation.md
CHANGELOG.md
```

## Current Ground Truth

The project has progressed substantially beyond the previous documentation baseline.

### Implemented

```text
JPA foundation
repositories
DTOs
exception handling

Agent runtime
AgentRegistry
AgentConfig
four concrete agents

AiModelGateway
ModelRouter
Groq integration

StructuredAiModelGateway
CEOPlanner
PlannedTask
ExecutionPlanBuilder

ToolRegistry
ToolConfiguration
FinancialCalculatorTool

WorkflowEngine
TaskDependencyResolver
WorkflowTaskDispatcher
TaskContextBuilder
WorkflowResult

targeted unit/integration tests
```

### Not Yet Implemented

```text
CEO final synthesis
RequestService
WorkflowService
REST controllers
runtime ↔ persistence integration
policy engine
approval system
audit persistence
authentication/authorization
parallel workflow execution
production integrations
CoALA memory infrastructure
```

## Canonical Architecture

```text
Business Objective
        ↓
CEOPlanner
        ↓
ExecutionPlan
        ↓
WorkflowEngine
        ↓
AgentRegistry
        ├── CFO
        ├── Engineering
        └── Infrastructure
        ↓
AgentResult
        ↓
CEO synthesis
        ↓
Final outcome
```

Core rule:

```text
CEO decides WHAT.
Workflow decides WHEN / WHETHER.
Agent decides HOW.
Tool decides HOW external action is performed.
```

## Important Design Decisions

- Exactly four MVP agents.
- Workflow-mediated collaboration.
- No unrestricted agent-to-agent chat.
- Runtime objects are not automatically JPA entities.
- `WorkflowTask`, `AgentTask`, and `AgentExecution` remain distinct.
- Tool names are declarative configuration identifiers.
- `ToolRegistry` maps names to callbacks but does not perform authorization.
- `ModelRouter` owns AgentType-to-config mapping.
- `AgentContext` is the working-memory extension point.
- CoALA memory is deferred.
- Parallel execution is deferred.
- REST is resource-oriented.
- Deterministic application code remains authoritative for arithmetic, state, policy, authorization, and side effects.

## Status Vocabulary

```text
IMPLEMENTED
PARTIAL
PLANNED
DEFERRED
```

Architectural target state must not be mistaken for implemented code.
