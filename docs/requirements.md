# FoundryAI — Requirements

> **Status:** Ground-truth update — 2026-09-16

## 1. Product Objective

The MVP demonstrates a controlled agentic workflow with exactly four agent roles:

```text
CEO
CFO
ENGINEERING
INFRASTRUCTURE
```

The MVP does not attempt to provide autonomous production operations, complete enterprise authorization, or a distributed worker platform.

## 2. Functional Requirements

### FR-1 — Business Requests

The application accepts a business objective through the request/application layer.

A request is persisted with a unique identifier and lifecycle status.

### FR-2 — Structured Planning

CEO planning produces specialist work containing:

```text
taskKey
agentType
objective
dependencies
```

`ExecutionPlanBuilder` converts model-facing task keys into runtime UUIDs.

### FR-3 — Workflow Delegation

The CEO planner creates specialist tasks but does not execute concrete specialist agents directly.

Execution occurs through:

```text
WorkflowEngine
→ WorkflowTaskDispatcher
→ AgentRegistry
→ Agent
```

### FR-4 — Dependency Management

The workflow runtime shall:

- validate dependency references,
- execute only runnable tasks,
- block tasks after dependency failure,
- detect no-progress/circular execution conditions.

These behaviors are implemented.

### FR-5 — Context Propagation

Dependent tasks receive dependency information through:

```text
AgentTask.context.data["dependencyResults"]
```

The current builder exposes each dependency as a `Map<String,Object>` containing selected result fields.

### FR-6 — CFO

Current:

- financial reasoning,
- financial recommendation,
- deterministic financial calculator tool.

Future:

- real financial data retrieval,
- forecasting integrations,
- cash runway analysis,
- richer simulations.

### FR-7 — Engineering

Current:

- technical reasoning,
- architecture/security analysis,
- implementation planning.

Future:

- repository operations,
- issue creation,
- pull requests,
- controlled code review and execution.

### FR-8 — Infrastructure

Current:

- infrastructure reasoning,
- deployment planning,
- scalability/reliability analysis.

Future:

- cloud APIs,
- Terraform tooling,
- deployment inspection,
- controlled infrastructure execution.

### FR-9 — Tool Contracts

Current tool capability is mediated through `ToolRegistry`.

Future mature tools should additionally specify:

```text
permissions
action classification
risk
idempotency
timeout
retry policy
audit requirements
```

### FR-10 — Action Classification

Target categories:

```text
READ
PROPOSE
EXECUTE
```

The classification is architectural guidance at present; there is no complete policy engine enforcing it.

### FR-11 — Human Approval

Future consequential actions shall be eligible for human approval.

No complete approval workflow is currently implemented.

### FR-12 — Authorization

Authorization must remain outside the LLM.

The current MVP does not implement complete authentication/authorization.

### FR-13 — Auditability

Future audit records should cover:

```text
requests
plans
agent executions
tool invocations
policy decisions
approvals
results
failures
```

Durable audit persistence is not currently implemented.

### FR-14 — Workflow Durability

Current workflows use runtime execution plus persisted request/workflow/task/execution records.

The implementation is not yet a distributed durable workflow engine capable of transparently surviving every failure mode.

### FR-15 — API

Current application endpoints expose request/workflow behavior.

Future API expansion may add:

```text
plan retrieval
workflow cancellation
approval operations
audit retrieval
versioned endpoints
```

### FR-16 — CEO Synthesis

Implemented.

Specialist results are aggregated by `CEOSynthesizer`, which calls the CEO model through `AiModelGateway` and returns `CEORecommendationResponseDTO`.

## 3. Non-Functional Requirements

The target system should preserve:

- clear layer boundaries,
- deterministic application controls around LLM reasoning,
- bounded workflow behavior,
- explicit typed DTOs,
- testability,
- extensibility without adding direct agent-to-agent coupling.

## 4. Explicit MVP Non-Goals

The MVP does not:

- autonomously run real company finances,
- autonomously deploy production,
- autonomously merge protected production code,
- provide regulated financial advice,
- implement unrestricted user-created agents,
- operate as a general-purpose AGI platform.
