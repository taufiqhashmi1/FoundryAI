# FoundryAI — Tool Architecture

> **Status:** Updated 2026-09-08  
> **Implementation:** Tool registry and deterministic financial calculator are implemented.

## 1. Purpose

Tools are the controlled execution layer between agents and application capabilities.

Current:

```text
Agent
 ↓
AiModelGateway
 ↓
ToolRegistry
 ↓
ToolCallback
 ↓
Tool implementation
```

Future:

```text
Tool request
 ↓
Capability check
 ↓
Policy
 ↓
Approval
 ↓
Adapter
 ↓
External system
```

## 2. Tool Registry

Implemented:

```text
ToolRegistry
```

It builds:

```text
tool name → ToolCallback
```

from Spring AI `ToolCallbackProvider` callbacks.

Duplicate tool names fail fast.

It does not perform authorization.

## 3. Agent Tool Configuration

Agent settings declare tool identifiers:

```properties
foundryai.agents.ceo.tools[0]=financial-calculator
foundryai.agents.cfo.tools[0]=financial-calculator
```

`AiModelGatewayImpl` resolves these identifiers through `ToolRegistry`.

## 4. Current Tool

```text
financial-calculator
```

Implementation:

```text
FinancialCalculatorTool
```

Behavior:

```text
monthlyCost × months
```

Uses `BigDecimal`.

Validation:

- monthly cost cannot be null,
- monthly cost cannot be negative,
- months must be greater than zero.

## 5. Current Tool Test

The CEO integration test verifies:

```text
monthlyCost = 1250.50
months = 12
result = 15006.00
```

The test also verifies invocation of the actual Spring-managed tool bean using `@MockitoSpyBean`.

## 6. Tool Categories

### READ

```text
getCashBalance
getTransactions
analyzeRepository
inspectInfrastructure
```

### PROPOSE

```text
createIssue
createPullRequest
generateTerraform
```

### EXECUTE

```text
mergePullRequest
deployProduction
modifyBudget
executePurchase
rollbackProduction
```

EXECUTE requires stronger future authorization.

## 7. Future Tool Contract

A mature tool should define:

```text
name
description
version
input schema
output schema
agent permissions
action classification
risk classification
idempotency strategy
timeout
retry policy
audit policy
```

## 8. Future Tool Context

Potential:

```text
userId
tenantId
workflowId
taskId
agent type/id
executionId
authorizationContext
correlationId
```

## 9. Execution Rules

1. Never trust model-generated parameters blindly.
2. Validate typed inputs.
3. Never let the model choose authorization.
4. Never execute destructive actions merely because the model says they are necessary.
5. Persist important executions once execution persistence exists.
6. Make side-effecting operations idempotent where possible.

## 10. Future CFO Tools

```text
getCashBalance
getTransactions
getMonthlyBurn
getRevenue
calculateRunway
forecastCashFlow
analyzeExpenses
simulateHiringPlan
```

## 11. Future Engineering Tools

```text
analyzeRepository
inspectIssues
breakDownFeature
estimateWork
createIssue
createPullRequest
reviewCode
runTests
```

`runTests` eventually requires isolation.

## 12. Future Infrastructure Tools

```text
inspectInfrastructure
estimateCloudCost
generateTerraform
validateTerraform
createInfrastructurePR
checkDeployment
```

Future high-impact capabilities:

```text
deployProduction
rollbackDeployment
```

## 13. Future Tool Result Model

Potential envelope:

```json
{
  "success": true,
  "status": "COMPLETED",
  "data": {},
  "warnings": [],
  "errors": [],
  "metadata": {}
}
```

The current callback integration does not require introducing this envelope yet.

## 14. Retry Policy

Read-only provider failures may be retryable.

Unknown side effects must not be blindly retried.

```text
createIssue()
 ↓
timeout
 ↓
outcome unknown
 ↓
lookup/idempotency
 ↓
retry only if safe
```

## 15. Tool Versioning

Future:

```text
estimateCloudCost:v1
estimateCloudCost:v2
```

## 16. MVP Principle

Prefer:

```text
Agent → Tool → Deterministic Service
```

over:

```text
Agent → LLM → LLM → LLM → External API
```

Use the model for reasoning and interpretation; use normal software for calculation, validation, persistence, authorization, and execution.
