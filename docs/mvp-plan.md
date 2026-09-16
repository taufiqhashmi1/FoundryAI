# FoundryAI — MVP Implementation Plan

> **Status:** Ground-truth update — 2026-09-16

## 1. Objective

The MVP is intended to prove the complete core agentic workflow without prematurely introducing distributed infrastructure, persistent memory, broad enterprise integrations, or a complete policy engine.

## 2. Current Implemented Flow

```text
Business Objective
      ↓
CEOPlanner
      ↓
PlannedTask[]
      ↓
ExecutionPlanBuilder
      ↓
ExecutionPlan
      ↓
WorkflowEngine
      ↓
TaskDependencyResolver
      ↓
TaskContextBuilder
      ↓
WorkflowTaskDispatcher
      ↓
CFO / Engineering / Infrastructure
      ↓
AgentResult
      ↓
CEOSynthesizer
      ↓
CEORecommendationResponseDTO
```

## 3. Phase Status

### Phase 1 — Foundation — COMPLETE

```text
Spring Boot
Java 25
Maven
MySQL/JPA
four JPA entities
repositories
DTOs
exception handling
```

### Phase 2 — Agent Runtime — COMPLETE

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
four concrete agents
```

### Phase 3 — AI Runtime — COMPLETE

```text
AiModelGateway
AiModelGatewayImpl
ModelRouter
Spring AI 2.0.1
Groq
```

### Phase 4 — Structured Planning — COMPLETE

```text
CEOPlanner
PlannedTask
ExecutionPlan
ExecutionPlanBuilder
```

### Phase 5 — Tool Runtime — COMPLETE FOR MVP

```text
ToolRegistry
ToolConfiguration
FinancialCalculatorTool
```

### Phase 6 — Workflow Runtime — COMPLETE

```text
TaskDependencyResolver
WorkflowTaskDispatcher
TaskContextBuilder
WorkflowEngine
WorkflowResult
WorkflowTaskStatus
```

Current behavior:

```text
validation
dependency resolution
dependency blocking
sequential dispatch
context propagation
result collection
failure conversion
cycle/no-progress detection
```

### Phase 7 — CEO Synthesis — COMPLETE

Implemented:

```text
CEOSynthesizer
CEORecommendationResponseDTO
AiModelGateway.synthesize(...)
Spring AI structured output + validateSchema()
```

### Phase 8 — Application Services — IMPLEMENTED

```text
RequestService
WorkflowService
```

They connect the request/workflow lifecycle with persistence and the runtime execution flow.

### Phase 9 — REST API — IMPLEMENTED FOR CURRENT MVP

```text
RequestController
WorkflowController
```

`ApprovalController` exists as a future-facing component.

A tested workflow execution request has returned a completed workflow and CEO recommendation through Postman.

## 4. Current MVP Boundaries

Not implemented:

```text
full authentication
authorization engine
policy engine
human approval workflow
audit event persistence
parallel workflow execution
distributed workers
external production integrations
persistent CoALA memory
```

## 5. Current Testing State

Core tests exist for:

- agents,
- planner,
- tools,
- workflow engine,
- task context,
- CEO end-to-end behavior.

The latest supplied Maven run before test realignment reported 57 tests, with 10 failures and 1 error. Those failures were primarily test-contract mismatches around `TaskContextBuilder` and `WorkflowEngine`. The corresponding tests have since been updated; a fresh full-suite result is still pending.

## 6. MVP Completion Criteria

For the current scope, the implementation should be considered operationally complete when:

1. the full Maven test suite passes after the latest test updates,
2. the demonstrated Postman workflow remains successful,
3. documentation matches the current source,
4. no obsolete architecture abstractions remain.

After those checks, feature growth should move to a separately defined Phase 2.
