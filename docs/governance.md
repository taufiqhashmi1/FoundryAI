# FoundryAI — AI Governance and Control Model

> **Status:** Ground-truth update — 2026-09-16
> **Implementation status:** Governance model is defined, but production policy/approval enforcement is not implemented.

## 1. Purpose

Governance defines how an eventual FoundryAI system distinguishes reasoning from authorization and consequential action.

Current MVP principle:

> Never use the LLM as a security or authorization boundary.

## 2. Responsibility Model

### LLM

Responsible for:

- interpretation,
- planning,
- reasoning,
- recommendation,
- tool selection.

### Application

Responsible for:

- validation,
- workflow state,
- persistence,
- deterministic calculation,
- future authorization,
- policy enforcement,
- tool execution.

### Human

Responsible for future consequential approvals and exceptions.

## 3. Action Classification

Target model:

```text
READ
PROPOSE
EXECUTE
```

Examples:

```text
READ
  inspect repository
  inspect infrastructure
  read financial data

PROPOSE
  create issue
  create pull request
  generate infrastructure configuration

EXECUTE
  merge protected code
  deploy production
  modify material budget
  execute material purchase
```

This classification is architectural guidance; no complete action-policy engine currently enforces it.

## 4. Risk and Environment

Future model:

```text
Risk:
LOW
MEDIUM
HIGH
CRITICAL

Environment:
LOCAL
DEVELOPMENT
STAGING
PRODUCTION
```

Risk must be determined by application policy and context, not by model opinion.

## 5. Current Status

Not implemented as a policy subsystem:

```text
ActionType
RiskLevel
Environment enforcement
PolicyDecision
PolicyEvaluator
Approval
```

`AgentConfig.tools` is an allowlist/capability configuration. `ToolRegistry` is a registry, not an authorization engine.

## 6. Agent Governance Intent

### CEO

May:

- plan,
- delegate,
- synthesize,
- recommend.

The CEO does not receive unrestricted specialist capabilities.

### CFO

May:

- analyze financial information,
- perform/trigger deterministic calculations,
- produce scenarios,
- recommend.

It must not autonomously make material financial commitments.

### Engineering

May eventually:

- inspect code,
- prepare issues/PRs,
- run controlled tests.

Protected production changes remain approval-controlled in the target architecture.

### Infrastructure

May eventually:

- analyze architecture,
- estimate costs,
- prepare infrastructure changes.

Production deployment is approval-controlled in the target architecture.

## 7. Approval

Future consequential actions should produce approval records bound to exact action parameters.

Target decision values:

```text
ALLOW
DENY
APPROVAL_REQUIRED
```

## 8. Current Security Reality

The current MVP does not implement the complete governance pipeline. This document therefore distinguishes intended controls from capabilities currently present in code.
