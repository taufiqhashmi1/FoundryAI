# FoundryAI — Project Progress & Ground-Truth Handoff

> **Canonical project state as of 2026-09-08.**

## 1. Project

**FoundryAI — Agentic AI Operating System for a FinTech Startup**

Base package:

```text
com.taufiqhashmi.foundryai
```

Goal:

> Build a minimal, scalable, extensible agentic-AI MVP rather than prematurely building a distributed multi-agent platform.

## 2. Technology

```text
Java 25
Spring Boot 4.1.1
Spring AI 2.0.1
Maven
MySQL 8.0.40
Hibernate 7.4.5.Final
Groq
```

Development database:

```text
jdbc:mysql://localhost:3306/foundryai
```

## 3. Exact Package Structure

```text
com.taufiqhashmi.foundryai
├── agents
├── ai
├── controllers
├── dtos
├── entities
├── exceptions
├── repositories
├── services
├── tools
└── workflows
```

## 4. Foundation — IMPLEMENTED

JPA entities:

```text
Request
Workflow
WorkflowTask
AgentExecution
```

Repositories:

```text
RequestRepository
WorkflowRepository
WorkflowTaskRepository
AgentExecutionRepository
```

DTOs:

```text
CreateRequestDTO
ErrorResponseDTO
RequestResponseDTO
ValidationErrorResponseDTO
WorkflowResponseDTO
WorkflowTaskResponseDTO
```

Exceptions:

```text
BadRequestException
ResourceNotFoundException
GlobalExceptionHandler
```

## 5. Agent Runtime — IMPLEMENTED

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

Current types:

```text
CEO
CFO
ENGINEERING
INFRASTRUCTURE
```

`AgentResult` uses:

```text
SUCCESS
FAILURE
```

rather than a mutable success boolean.

## 6. Concrete Agents — IMPLEMENTED

```text
CEOAgent
CFOAgent
EngineeringAgent
InfrastructureAgent
```

All four are thin adapters around `AiModelGateway`.

## 7. AI Runtime — IMPLEMENTED

```text
AiModelGateway
AiModelGatewayImpl
ModelRouter
```

```text
Agent
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

Configuration:

```text
model
systemPrompt
temperature
tools
includeReasoning
```

## 8. Structured CEO Planning — IMPLEMENTED

```text
StructuredAiModelGateway
StructuredAiModelGatewayImpl
CEOPlanner
PlannedTask
ExecutionPlan
ExecutionPlanBuilder
```

Flow:

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

A real-model integration test has verified structured planning.

## 9. Tool Runtime — IMPLEMENTED FOR CURRENT MVP

```text
ToolRegistry
ToolConfiguration
FinancialCalculatorTool
```

Current tool:

```text
financial-calculator
```

The tool performs deterministic `BigDecimal` arithmetic.

## 10. Workflow Runtime — IMPLEMENTED

```text
ExecutionPlan
PlannedTask
ExecutionPlanBuilder
TaskDependencyResolver
WorkflowTaskDispatcher
TaskContextBuilder
WorkflowEngine
WorkflowResult
WorkflowTaskStatus
```

Current engine handles:

- plan validation,
- duplicate task detection,
- unknown dependency rejection,
- cycle/no-progress detection,
- runnable-task resolution,
- dependency blocking,
- task dispatch,
- result collection,
- runtime failure handling,
- context construction,
- dependency result propagation.

## 11. Workflow Semantics

If:

```text
CFO → FAILED
```

then a dependent task becomes:

```text
BLOCKED
```

Unknown dependencies are rejected.

Circular/no-progress plans are rejected.

Dispatch exceptions become failed `AgentResult` values.

## 12. Context Propagation

Current:

```text
AgentTask.context.data["dependencyResults"]
```

Value:

```text
Map<UUID, AgentResult>
```

Verified:

```text
CFO
 ↓
CFO AgentResult
 ↓
TaskContextBuilder
 ↓
Engineering AgentTask.context
 ↓
Engineering
```

## 13. Current Execution Model

The workflow engine is currently sequential.

Independent tasks are recognized through the dependency graph, but are not yet executed concurrently.

Parallel execution is future work.

## 14. Tests

Verified test categories:

```text
CEO real-model execution
CEO tool calling
CEO structured planning
workflow dependency behavior
workflow failure/blocking
workflow validation
TaskContextBuilder
dependency-result propagation
```

## 15. Current Architecture

```text
Business Objective
       ↓
CEOPlanner
       ↓
ExecutionPlan
       ↓
WorkflowEngine
       ├── TaskDependencyResolver
       ├── TaskContextBuilder
       └── WorkflowTaskDispatcher
                    ↓
              AgentRegistry
              ├── CFO
              ├── Engineering
              └── Infrastructure
                    ↓
              AgentResult
```

## 16. CEO Synthesis — NEXT

```text
CFO result
Engineering result
Infrastructure result
        ↓
CEO synthesis
        ↓
Final business outcome
```

This should not be implemented as a final CFO task.

## 17. Application Services — NOT YET IMPLEMENTED

```text
RequestService
WorkflowService
```

These should connect durable request/workflow state to the runtime.

## 18. REST — NOT YET IMPLEMENTED

Planned:

```text
RequestController
WorkflowController
ApprovalController
```

## 19. Governance — NOT YET IMPLEMENTED

Future:

```text
PolicyEvaluator
PolicyDecision
Approval
ActionType
RiskLevel
Environment
```

## 20. CoALA — DEFERRED

> Design for memory now; implement memory later.

Do not add memory/vector/embedding infrastructure yet.

## 21. Immediate Next Step

```text
1. CEO synthesis
2. RequestService / WorkflowService
3. runtime ↔ persistence integration
4. end-to-end orchestration test
5. REST API
6. retries/timeouts
7. governance/approval
8. audit/observability
```

## 22. Rules for Future Sessions

1. Do not invent implementation status.
2. Preserve exact plural package names.
3. Keep runtime and persistence separate.
4. Do not conflate `WorkflowTask`, `AgentTask`, and `AgentExecution`.
5. CEO must orchestrate through structured plans/workflows.
6. Do not add CoALA memory infrastructure yet.
7. Do not create JPA entities for every runtime concept.
8. Do not create per-agent controllers.
9. Keep repositories minimal.
10. Use DTOs at REST boundaries.
11. Do not introduce parallelism before sequential workflow semantics are stable.
12. Keep `ToolRegistry` as a registry, not an authorization engine.
13. Keep `ModelRouter` as the single AgentType → AgentSettings mapping point.
14. Do not add abstractions merely to satisfy SOLID.
