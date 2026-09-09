# FoundryAI — Requirements

> **Status:** Updated 2026-09-08

## 1. Purpose

The MVP proves the platform architecture with four agents:

```text
CEO
CFO
ENGINEERING
INFRASTRUCTURE
```

Security and Operations remain future scope.

## 2. Functional Requirements

### FR-1 — User Requests

The system shall eventually allow an authenticated user to submit a natural-language business objective.

The request receives a unique identifier and is persisted.

### FR-2 — Structured Planning

CEO planning shall produce structured specialist work containing:

- task key,
- responsible agent,
- objective,
- dependencies.

Future fields may include assumptions, expected outputs, risk, and approval requirements.

### FR-3 — Agent Delegation

The orchestrator shall delegate through the workflow runtime.

Agents must not directly invoke arbitrary concrete agents.

### FR-4 — Dependency Management

The workflow runtime shall:

- validate dependency references,
- prevent execution before dependencies complete,
- block dependents after prerequisite failure,
- reject circular/no-progress plans.

This is implemented in the current runtime engine.

### FR-5 — Context Propagation

Dependent agents shall receive relevant completed dependency results through `AgentTask.context`.

Current representation:

```text
context.data["dependencyResults"]
```

### FR-6 — CFO

Eventually support:

- financial retrieval,
- deterministic calculations,
- forecasting,
- expense analysis,
- hiring simulation,
- financial summaries.

Current tool:

```text
financial-calculator
```

### FR-7 — Engineering

Eventually support:

- repository analysis,
- issue inspection,
- feature decomposition,
- estimation,
- issue creation,
- PR creation,
- code review,
- test execution.

The agent runtime itself is implemented.

### FR-8 — Infrastructure

Eventually support:

- architecture analysis,
- resource estimation,
- cloud-cost estimation,
- Terraform generation/validation,
- infrastructure PRs,
- deployment inspection.

The agent runtime itself is implemented.

### FR-9 — Tool Contracts

Future tools shall define:

- name,
- description,
- input/output,
- permissions,
- action classification,
- risk,
- idempotency,
- failure behavior.

### FR-10 — Action Classification

```text
READ
PROPOSE
EXECUTE
```

Material EXECUTE actions require policy authorization and, where configured, human approval.

### FR-11 — Human Approval

Future consequential actions shall create approval records containing exact action parameters and decision metadata.

### FR-12 — Authorization

Authorization must be outside the LLM.

### FR-13 — Auditability

Future durable audit records shall cover requests, plans, agent invocations, tool calls, policy decisions, approvals, results, and failures.

### FR-14 — Workflow Durability

Long-running workflows shall eventually survive:

- application restart,
- model failure,
- tool failure,
- approval waits,
- external delays.

The current workflow engine is in-memory; the JPA foundation exists but is not yet integrated into the runtime loop.

### FR-15 — API

Future API shall support request creation/retrieval, workflow/task retrieval, plan retrieval, cancellation, approval operations, and audit retrieval.

### FR-16 — CEO Synthesis

The platform shall aggregate specialist results and produce a final CEO-level recommendation.

This is the next orchestration capability.

## 3. Non-Functional Requirements

### NFR-1 — Scalability

Logical architecture must support later horizontal scaling.

### NFR-2 — Reliability

Transient failures should eventually be retryable with bounded policy.

### NFR-3 — Security

Secrets must not be embedded in prompts, source, ordinary logs, or persistent model context.

### NFR-4 — Observability

Eventually expose structured logs, correlation IDs, workflow IDs, execution IDs, latency, and failure metrics.

### NFR-5 — Determinism

Financial calculations, authorization, policy, and workflow state transitions should be deterministic.

### NFR-6 — Extensibility

Adding an agent must not require rewriting core orchestration.

Adding a tool must not require changing unrelated agents.

### NFR-7 — Cost Control

Track model/token metadata where available and introduce workflow/agent budgets.

### NFR-8 — Testability

Agents and workflows must be independently testable.

## 4. Current Acceptance

Already demonstrated:

```text
CEO model execution
CEO tool call
CEO structured planning
workflow dependency execution
failure/blocking semantics
context construction
dependency result propagation
```

Next acceptance:

```text
business objective
 ↓
CEO plan
 ↓
workflow execution
 ↓
specialist results
 ↓
CEO synthesis
 ↓
final outcome
```

## 5. Out of Scope

- Security Agent implementation
- Operations Agent implementation
- real banking integrations
- unrestricted cloud execution
- autonomous production deployment
- arbitrary user-created agent code
- vector memory
- distributed microservices
- Kubernetes/Kafka/Redis
- custom model training
