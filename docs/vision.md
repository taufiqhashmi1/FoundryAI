# FoundryAI — Product Vision

> **Status:** Updated 2026-09-08

## 1. Product

**FoundryAI** is an AI operating system for a startup.

A user expresses a high-level business objective. FoundryAI decomposes it into controlled work, routes work to specialist agents, uses deterministic tools, aggregates results, and eventually coordinates policy-controlled execution.

The target is a controlled agentic execution platform, not a collection of chatbots.

## 2. Problem

Startup workflows span:

- finance,
- source repositories,
- infrastructure,
- vendors,
- operations.

Humans repeatedly translate business objectives into specialist work.

LLMs provide reasoning but do not inherently provide authorization, persistent workflow state, reliable execution, auditability, or domain separation.

## 3. Target Users

Initial personas:

- startup CEO/founder,
- engineering leader,
- operations leader.

The MVP uses a simulated/controlled FinTech environment.

## 4. Core User Promise

Eventually:

> “Plan our next product launch.”

should produce:

1. a decomposed plan,
2. specialist analysis,
3. dependencies,
4. financial and technical impact,
5. recommendations,
6. approval requirements,
7. auditable workflow history.

## 5. Current MVP Scope

Exactly:

```text
CEO / Orchestrator
CFO
Engineering
Infrastructure
```

Current runtime capability:

```text
structured CEO planning
specialist execution
dependency resolution
dependency-result context propagation
deterministic tool calling
```

Application-level request/API orchestration remains future work.

## 6. Target Product Workflow

```text
User Request
 ↓
CEO
 ↓
Structured Plan
 ↓
CFO / Engineering / Infrastructure
 ↓
Structured Results
 ↓
CEO Synthesis
 ↓
Recommendation
 ↓
Policy / Approval
 ↓
Controlled Execution
```

## 7. Future Agent Topology

```text
CEO
 ├── CFO
 ├── Engineering
 ├── Infrastructure
 ├── Security
 └── Operations
```

## 8. Product Principles

### Business intent first

Users interact with objectives rather than low-level tools.

### Agents are specialists

Each agent has narrow responsibilities and explicit capabilities.

### LLMs reason; systems enforce

The model recommends. Application code validates and authorizes.

### Consequential actions require control

Material actions use policy and approval.

### Important state is observable

Workflow, tool, approval, and execution state should be traceable.

### Design for extension

New agents and tools should not require replacing the core runtime.

## 9. Current Success Milestone

The runtime has demonstrated:

```text
Business objective
 ↓
CEO structured plan
 ↓
ExecutionPlan
 ↓
WorkflowEngine
 ↓
Specialist agents
 ↓
Dependency-aware context
 ↓
Agent results
```

Next milestone:

```text
specialist results
 ↓
CEO synthesis
 ↓
final outcome
```

followed by service and REST integration.

## 10. Non-Goals

The MVP should not:

- autonomously run real company finances,
- autonomously deploy production,
- autonomously merge protected production code,
- provide regulated financial advice,
- replace security professionals,
- integrate every enterprise SaaS product,
- support arbitrary user-created autonomous agents,
- become a general-purpose AGI platform.

## 11. Long-Term Vision

After the core runtime is proven:

- Security and Operations agents,
- real GitHub/AWS integrations,
- richer policy,
- human approval,
- audit/event infrastructure,
- CoALA-compatible memory,
- semantic retrieval,
- additional specialists,
- scalable worker topology.
