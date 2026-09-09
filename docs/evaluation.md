# FoundryAI — Evaluation Strategy

> **Status:** Updated 2026-09-08  
> **Current status:** Foundational unit/integration evaluation is implemented; full evaluation harness remains future work.

## 1. Purpose

Evaluation determines whether the agentic system is reliable, not merely fluent.

Evaluate:

- planning,
- delegation,
- dependency correctness,
- tool selection,
- tool arguments,
- factual grounding,
- workflow behavior,
- failure propagation,
- authorization,
- safety,
- approvals,
- recovery,
- cost,
- latency.

## 2. Current Evaluation Layers

```text
Unit Tests                 ← implemented
Tool Tests                 ← implemented for calculator
Agent Integration Tests   ← implemented
Workflow Unit Tests       ← implemented
Context Tests             ← implemented
Structured Planning Test  ← implemented
Policy/Security Tests     ← future
End-to-End Request Flow   ← future
Production Evaluation     ← future
```

## 3. Verified Coverage

### CEO execution

Real-model CEO execution is tested.

### CEO tool calling

The `financial-calculator` integration test verifies:

```text
monthlyCost = 1250.50
months = 12
result = 15006.00
```

The test also verifies actual invocation using a Spring-managed `@MockitoSpyBean`.

### CEO structured planning

Real Groq structured planning has been verified through `CEOPlannerIntegrationTest`.

### Workflow engine

The workflow suite covers:

- independent tasks,
- duplicate task IDs,
- circular dependencies,
- failed dependencies,
- null plans,
- dispatcher exceptions,
- transitive blocking,
- result collection,
- failed task recording,
- unknown dependencies,
- dependency ordering,
- missing task IDs,
- empty plans.

### TaskContextBuilder

Coverage includes:

- dependency result construction,
- no dependencies,
- missing dependency result,
- preservation of existing context.

### Context propagation

Verified:

```text
CFO AgentResult
 ↓
TaskContextBuilder
 ↓
Engineering AgentTask.context
 ↓
Engineering receives CFO result
```

## 4. Deterministic Tool Evaluation

Current example:

```text
monthly cost = 1250.50
months = 12
expected = 15006.00
```

The LLM is not the arithmetic authority.

## 5. CEO Evaluation

Evaluate:

- specialist selection,
- task completeness,
- unique task keys,
- dependency correctness,
- structured output validity,
- absence of unnecessary synthesis delegation,
- final synthesis quality.

## 6. Workflow Evaluation

Expected semantics:

```text
dependency satisfied → task runs
dependency failed → dependent task blocked
unknown dependency → plan rejected
cycle → plan rejected
dispatch failure → task failure
```

Also verify dependency-result context propagation.

## 7. Golden Scenarios

### Product Launch

```text
CEO
 ├── CFO
 ├── Engineering
 └── Infrastructure
```

### Hiring

```text
CEO
 ↓
CFO
 ↓
financial recommendation
```

### Unsafe Production Deployment

```text
Policy
 ↓
APPROVAL_REQUIRED
```

### Tool Failure

```text
failure
 ↓
classification
 ↓
bounded retry or task failure
```

### Prompt Injection

External content is untrusted data.

### Duplicate Execution

Unknown external outcomes must be reconciled before retry.

## 8. Metrics

Reliability:

```text
workflow success rate
task success rate
tool success rate
retry rate
failure recovery rate
```

Quality:

```text
plan completeness
delegation accuracy
dependency accuracy
tool selection accuracy
tool argument accuracy
structured output validity
```

Safety:

```text
unauthorized action rate
approval bypass rate
secret leakage rate
prompt injection success rate
cross-tenant access rate
```

Performance:

```text
workflow latency
agent latency
tool latency
model latency
```

Cost:

```text
tokens/workflow
model cost/workflow
tool/provider cost
```

## 9. Targets

Engineering targets, not achieved measurements:

```text
structured output validity > 99%
unauthorized execution = 0%
approval bypass = 0%
critical secret leakage = 0%
core workflow completion > 95%
tool argument correctness > 98%
```

## 10. Regression

Changes to:

- prompts,
- models,
- agent tools,
- workflow semantics,
- dependency resolution,
- context construction

should trigger relevant tests.

Prompt changes are behavior changes.

## 11. Next Priority

1. CEO synthesis tests
2. product-launch end-to-end workflow
3. failure/retry tests
4. policy/approval tests
5. prompt-injection tests
6. idempotency tests
7. model/prompt regression
8. performance/cost evaluation
