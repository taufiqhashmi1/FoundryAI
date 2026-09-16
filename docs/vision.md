# FoundryAI — Product Vision

> **Status:** Ground-truth update — 2026-09-16

## 1. Product

**FoundryAI** is an AI operating system for a startup.

The current product is a controlled agentic application: a business objective is converted into specialist work, specialist reasoning is executed through a workflow runtime, deterministic tools provide controlled computation, and specialist results are synthesized into a CEO-level recommendation.

The target is a controlled execution platform, not a collection of independent chatbots.

## 2. Problem

Startup workflows span:

- finance,
- engineering,
- infrastructure,
- vendors,
- operations.

Humans repeatedly translate high-level business objectives into specialist work.

LLMs can provide reasoning but do not inherently provide authorization, durable workflow semantics, deterministic arithmetic, auditability, or safe external execution.

## 3. Target Users

Initial personas:

- startup CEO/founder,
- engineering leader,
- operations leader.

The MVP remains a controlled/simulated FinTech environment.

## 4. Current User Flow

The currently implemented core flow is:

```text
Business objective
      ↓
CEO planning
      ↓
specialist tasks
      ↓
dependency-aware workflow execution
      ↓
specialist results
      ↓
CEO synthesis
      ↓
recommendation
```

## 5. Current MVP Scope

Exactly:

```text
CEO / Orchestrator
CFO
Engineering
Infrastructure
```

Current capabilities include:

```text
structured planning
specialist execution
dependency resolution
dependency-result context propagation
deterministic financial tool use
CEO synthesis
request/workflow persistence
REST workflow execution
```

## 6. Target Product Workflow

The longer-term target adds controls beyond the current MVP:

```text
User Request
 ↓
CEO
 ↓
Structured Plan
 ↓
Specialists
 ↓
Structured Results
 ↓
CEO Synthesis
 ↓
Recommendation
 ↓
Policy
 ↓
Approval where required
 ↓
Controlled Execution
 ↓
Audit
```

The policy, approval, and audit stages are future capabilities.

## 7. Future Agent Topology

```text
CEO
 ├── CFO
 ├── Engineering
 ├── Infrastructure
 ├── Security
 └── Operations
```

Only the first four are currently implemented.

## 8. Product Principles

### Business intent first

The user expresses an objective rather than manually invoking individual specialist agents.

### Agents are specialists

Responsibilities are explicit and narrow.

### LLMs reason; systems enforce

The model proposes reasoning and recommendations. Application code owns state, validation, deterministic computation, and future authorization.

### Consequential actions require control

Material external actions should eventually pass through policy and approval.

### State is observable

Workflow, task, result, and execution state are persisted where the current application requires it.

### Design for extension

New agents and tools should plug into registries and contracts rather than requiring direct coupling.

## 9. Current Milestone

The runtime has demonstrated:

```text
business objective
 ↓
CEO structured plan
 ↓
ExecutionPlan
 ↓
WorkflowEngine
 ↓
specialist agents
 ↓
dependency-aware context
 ↓
AgentResult
 ↓
CEO synthesis
 ↓
final recommendation
```

A local Postman execution has returned a completed workflow with all three specialist tasks completed and a populated CEO recommendation.

## 10. Non-Goals

The current MVP should not be described as:

- an autonomous financial operator,
- a production deployment agent,
- a regulated financial adviser,
- a replacement for security professionals,
- a distributed autonomous organization,
- a general-purpose AGI platform.

## 11. Long-Term Vision

After the current MVP is stable:

- richer specialist agents,
- GitHub/cloud integrations,
- policy and approval enforcement,
- audit/event infrastructure,
- CoALA-compatible memory,
- semantic retrieval,
- bounded parallelism,
- distributed workers,
- stronger observability and production controls.
