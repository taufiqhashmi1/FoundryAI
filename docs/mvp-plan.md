# FoundryAI — MVP Implementation Plan

> **Status:** Updated 2026-09-08  
> **Current phase:** Runtime and workflow execution foundation substantially implemented.

## 1. Objective

Build a scalable first version that proves the core agentic lifecycle without prematurely building enterprise-scale infrastructure.

## 2. Current Core Flow

```text
Business Objective
  ↓
CEOPlanner
  ↓
Structured ExecutionPlan
  ↓
WorkflowEngine
  ↓
CFO / Engineering / Infrastructure
  ↓
AgentResult
  ↓
CEO synthesis
```

Tool path:

```text
Agent
 ↓
AiModelGateway
 ↓
ToolRegistry
 ↓
Deterministic Tool
```

## 3. Phase Status

### Phase 1 — Foundation — COMPLETE

- Spring Boot
- Java 25
- Maven
- MySQL/JPA
- four JPA entities
- repositories
- DTOs
- exceptions

### Phase 2 — Agent Runtime — COMPLETE

```text
Agent
AgentType
AgentTask
AgentContext
AgentResult
AgentResultStatus
AgentConfig
AgentRegistry
```

### Phase 3 — AI Runtime — COMPLETE

```text
AiModelGateway
AiModelGatewayImpl
ModelRouter
```

Integrated:

```text
Spring AI 2.0.1
Groq
```

### Phase 4 — Concrete Agents — COMPLETE

```text
CEOAgent
CFOAgent
EngineeringAgent
InfrastructureAgent
```

### Phase 5 — Structured CEO Planning — COMPLETE

```text
StructuredAiModelGateway
StructuredAiModelGatewayImpl
CEOPlanner
PlannedTask
ExecutionPlan
ExecutionPlanBuilder
```

Real-model structured planning is verified.

### Phase 6 — Tool Runtime — PARTIAL / CURRENT MVP COMPLETE

Implemented:

```text
ToolRegistry
ToolConfiguration
FinancialCalculatorTool
```

Current tool:

```text
financial-calculator
```

Full authorization-aware tool execution is future work.

### Phase 7 — Workflow Runtime — IMPLEMENTED

Implemented:

```text
TaskDependencyResolver
WorkflowTaskDispatcher
TaskContextBuilder
WorkflowEngine
WorkflowResult
WorkflowTaskStatus
```

Current behavior:

- validation,
- dependency resolution,
- blocking,
- dispatch,
- result collection,
- context propagation,
- cycle/no-progress detection,
- runtime failure conversion.

Current execution is sequential.

JPA persistence is not yet integrated into this runtime loop.

### Phase 8 — CEO Synthesis — NEXT

```text
specialist results
 ↓
CEO synthesis
 ↓
final business outcome
```

The planner should not create final synthesis as a CFO task.

### Phase 9 — Application Services — NEXT

Implement:

```text
RequestService
WorkflowService
```

They should connect request/workflow persistence to the runtime without becoming God services.

### Phase 10 — REST API — AFTER CORE ORCHESTRATION

Implement:

```text
RequestController
WorkflowController
ApprovalController
```

Initial endpoints:

```text
POST /api/v1/requests
GET /api/v1/requests/{id}
GET /api/v1/workflows/{id}
GET /api/v1/workflows/{id}/tasks
POST /api/v1/workflows/{id}/cancel
```

### Phase 11 — Governance / Approval — FUTURE

```text
ActionType
RiskLevel
Environment
PolicyDecision
PolicyEvaluator
Approval
```

### Phase 12 — Audit / Observability — FUTURE

Add durable lifecycle events after workflow/application boundaries stabilize.

### Phase 13 — Evaluation — IN PROGRESS

Implemented:

- agent integration tests,
- tool integration test,
- structured planner integration test,
- workflow tests,
- context builder tests,
- context propagation integration test.

Next:

- end-to-end product launch,
- CEO synthesis,
- retry/failure evaluation,
- policy bypass,
- prompt injection,
- idempotency.

### Phase 14 — CoALA Memory — DEFERRED

Design for memory now, implement later.

### Phase 15 — Production Hardening — FUTURE

Later:

- authentication,
- authorization,
- audit,
- idempotency,
- concurrency,
- transactions,
- Docker/Testcontainers,
- Flyway,
- production observability.

## 4. Non-Goals

Do not start with:

- Kubernetes,
- Kafka,
- Redis,
- microservices,
- complex memory,
- vector databases,
- arbitrary user-created agents,
- autonomous production deployment,
- complex event sourcing,
- custom model training.

## 5. Current Runtime Milestone

```text
business objective
 ↓
CEO structured plan
 ↓
ExecutionPlan
 ↓
WorkflowEngine
 ↓
CFO / Engineering / Infrastructure
 ↓
dependency-aware context
 ↓
AgentResults
```

The broader application MVP additionally requires:

```text
CEO synthesis
+
RequestService
+
WorkflowService
+
REST
```

## 6. Recommended Next Order

```text
1. CEO synthesis
2. RequestService / WorkflowService
3. runtime ↔ persistence integration
4. end-to-end orchestration test
5. REST API
6. retries/timeouts
7. governance/approval
8. audit/observability
9. evaluation expansion
10. production hardening
