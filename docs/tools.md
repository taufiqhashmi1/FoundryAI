# FoundryAI — Tool Architecture

> **Status:** Ground-truth update — 2026-09-16
> **Implementation:** Tool registry and deterministic financial calculator are implemented.

## 1. Purpose

Tools are the controlled execution boundary between agents and application capabilities.

Current path:

```text
Agent
 ↓
AiModelGatewayImpl
 ↓
AgentSettings.tools
 ↓
ToolRegistry
 ↓
ToolCallback
 ↓
Tool implementation
```

## 2. Tool Registry

`ToolRegistry` creates:

```text
tool name → ToolCallback
```

from a Spring AI `ToolCallbackProvider`.

It:

- rejects duplicate tool names,
- resolves tools by name,
- rejects missing/blank names,
- fails when a requested tool is not registered.

It does not perform authorization.

## 3. Tool Configuration

Agent configuration declares tool identifiers.

Example:

```properties
foundryai.agents.ceo.tools[0]=financial-calculator
foundryai.agents.cfo.tools[0]=financial-calculator
```

`AiModelGatewayImpl` resolves these identifiers through `ToolRegistry`.

There is no separate `AgentToolResolver`.

## 4. Current Tool

```text
financial-calculator
```

Implemented by:

```text
FinancialCalculatorTool
```

Behavior:

```text
monthlyCost × months
```

Signature:

```java
BigDecimal calculateAnnualCost(
    BigDecimal monthlyCost,
    int months
)
```

Although the method name says `calculateAnnualCost`, its arithmetic is explicitly based on the supplied number of months.

Validation:

```text
monthly cost cannot be null
monthly cost cannot be negative
months must be > 0
```

The implementation uses `BigDecimal`.

## 5. Tool Evaluation

A dedicated test verifies:

```text
1250.50 × 12 = 15006.00
```

The integration coverage also verifies invocation of the Spring-managed tool bean.

## 6. Tool Security Boundary

Current tool registration is not equivalent to authorization.

Future mature tool execution should include:

```text
capability check
policy decision
approval where required
adapter / external API call
audit event
```

## 7. Target Tool Categories

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

These are future tool examples, not current implemented tools.

## 8. Future Tool Contract

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

## 9. Execution Rules

1. Do not blindly trust model-generated parameters.
2. Validate typed tool inputs.
3. Do not let the model choose authorization.
4. Do not execute destructive operations merely because the model recommends them.
5. Persist important executions when durable tool-execution persistence exists.
6. Make side-effecting operations idempotent where possible.
