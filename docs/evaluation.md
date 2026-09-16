# FoundryAI — Evaluation Strategy

> **Status:** Ground-truth update — 2026-09-16
> **Current status:** Unit/integration evaluation exists for the core runtime and tools; final full-suite verification after the latest test alignment is still pending.

## 1. Purpose

Evaluation verifies that FoundryAI is behaving correctly, not merely producing plausible language.

Current focus:

- agent contracts,
- planning,
- structured outputs,
- dependency ordering,
- failure propagation,
- context propagation,
- tool behavior,
- workflow engine behavior,
- request/workflow integration.

Future evaluation will cover policy, authorization, approvals, auditability, production integrations, and resilience.

## 2. Current Test Layers

```text
Unit tests                         implemented
Tool tests                         implemented
Agent tests                        implemented
Planner integration tests         implemented
CEO end-to-end integration        implemented
Workflow unit tests                implemented
Context tests                      implemented
REST/Postman smoke validation      manually demonstrated
Policy/security evaluation         future
Production evaluation               future
```

## 3. Verified Functionality

### CEO end-to-end

A real-model CEO workflow test has executed successfully.

It demonstrated:

```text
business objective
    ↓
CEO structured plan
    ↓
specialist workflow execution
    ↓
specialist results
    ↓
CEO synthesis
```

### Structured output

The CEO end-to-end log showed a malformed specialist response being detected by Spring AI's `StructuredOutputValidationAdvisor`, followed by a successful structured workflow completion. The current design therefore relies on Spring AI structured output validation rather than manual JSON parsing.

### Tool

`FinancialCalculatorTool` has dedicated tests and integration coverage.

Example:

```text
monthlyCost = 1250.50
months = 12
expected = 15006.00
```

The tool uses `BigDecimal` and validates invalid inputs.

### Workflow engine

The test suite covers:

- independent tasks,
- dependency ordering,
- duplicate task IDs,
- circular dependencies,
- failed dependencies,
- transitive blocking,
- dispatcher exceptions,
- result collection,
- unknown dependencies,
- null plans,
- empty plans,
- missing task IDs.

### Context builder

The current test contract reflects the implementation:

```text
dependency result
    ↓
Map<String,Object>
    ↓
context.data["dependencyResults"]
```

The current implementation does not preserve an existing `AgentTask.context` when building a new context and silently omits missing dependency results.

## 4. Failure Semantics

Expected workflow semantics:

```text
dependency successful → dependent task may run
dependency failed     → dependent task BLOCKED
unknown dependency    → invalid plan
cycle/no progress     → execution rejected
dispatcher exception  → FAILED AgentResult
```

## 5. Most Recent Full Maven Run

The supplied full Maven run reported:

```text
Tests run: 57
Failures: 10
Errors: 1
```

Failures were concentrated in:

```text
TaskContextBuilderTest
WorkflowEngineTest
WorkflowEngineContextIntegrationTest
```

The root issue was primarily mismatch between the then-current tests and `TaskContextBuilder` behavior, plus an uninitialized `TaskContextBuilder` in part of `WorkflowEngineTest`.

The tests have since been updated to match the current implementation contract. A fresh full `mvn test` after those edits has not yet been captured.

## 6. Manual API Validation

A Postman request against the local application successfully returned a completed workflow containing:

```text
status = COMPLETED
CFO task = COMPLETED
ENGINEERING task = COMPLETED
INFRASTRUCTURE task = COMPLETED
CEO recommendation = populated
```

This validates the application path end-to-end at the HTTP level for the tested scenario.

## 7. Future Evaluation

Future additions should cover:

```text
authorization
policy decisions
human approvals
tenant isolation
audit integrity
timeout handling
provider outages
rate limiting
tool failure classification
idempotent side effects
parallel execution
cost/latency budgets
production integrations
```
