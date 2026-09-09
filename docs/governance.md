# FoundryAI — AI Governance and Control Model

> **Status:** Updated 2026-09-08  
> **Implementation status:** Governance is target architecture. Policy and approval enforcement are not implemented.

## 1. Purpose

Governance defines what the system may do, when it can act autonomously, and when a human must intervene.

```text
READ
 ↓
autonomous

PROPOSE
 ↓
reviewable artifact

EXECUTE
 ↓
policy
 ↓
approval where required
```

## 2. Responsibility Model

### LLM

Responsible for:

- interpretation,
- planning,
- reasoning,
- recommendations,
- tool selection.

### Application

Responsible for:

- validation,
- state,
- authorization,
- policy enforcement,
- execution,
- audit.

### Human

Responsible for:

- consequential approvals,
- exceptions,
- high-impact decisions.

## 3. Action Classification

### READ

Examples:

```text
get cash balance
inspect repository
inspect infrastructure
```

Default: autonomous.

### PROPOSE

Examples:

```text
create issue
create PR
generate Terraform
prepare purchase request
```

Default: create a reviewable artifact.

### EXECUTE

Examples:

```text
merge protected code
deploy production
modify material budget
execute material purchase
```

Default: policy evaluation plus approval.

## 4. Risk

```text
LOW
MEDIUM
HIGH
CRITICAL
```

Risk is determined by action and environment, not by model opinion.

## 5. Environment

```text
LOCAL
DEVELOPMENT
STAGING
PRODUCTION
```

## 6. Current Status

Not implemented:

```text
ActionType
RiskLevel
Environment enforcement
PolicyDecision
PolicyEvaluator
Approval
```

The `AgentConfig` tool allowlist is capability configuration, not complete authorization.

`ToolRegistry` is a registry, not a policy engine.

## 7. Approval Matrix

| Action | Development | Staging | Production |
|---|---|---|---|
| Read data | Auto | Auto | Auto |
| Create issue | Auto | Auto | Auto |
| Create PR | Auto | Auto | Auto |
| Merge protected code | Policy | Approval | Approval |
| Deploy | Approval | Approval | Approval |
| Destructive change | Approval | Approval | Strong approval |
| Financial commitment | Approval | Approval | Approval |

## 8. Agent Governance

### CEO

May plan, delegate, synthesize, and recommend.

Does not inherit unrestricted specialist tools.

### CFO

May analyze financial data, calculate scenarios, forecast, and recommend.

Must not autonomously make material financial commitments.

### Engineering

May inspect code, prepare issues/PRs, and run controlled tests.

Protected production merges require approval.

### Infrastructure

May estimate cost and prepare/validate infrastructure changes.

Production deployment requires approval.

## 9. Policy Decision

Future:

```text
ALLOW
DENY
APPROVAL_REQUIRED
```

Inputs may include:

```text
agent
action
environment
risk
user permissions
approval state
security state
```

## 10. Approval Integrity

Approval must bind to exact action parameters.

```text
approved:
deploy version 1.4.2 to staging

must not authorize:
deploy version 1.5.0 to production
```

Use an immutable action payload/hash when implemented.

## 11. Governance Decision Record

Future:

```text
policy
policyVersion
inputs
decision
reason
timestamp
workflowId
actionId
```

## 12. Security Agent

Future:

```text
Security Agent
 ↓
Finding
 ↓
Policy
 ↓
ALLOW / DENY / APPROVAL_REQUIRED
```

The Security Agent is not the final enforcement authority.

## 13. Future Governance

Potential:

- policy-as-code,
- multi-party approval,
- segregation of duties,
- risk scoring,
- regulatory controls,
- provider allowlists,
- organization-specific policies,
- approval delegation,
- controlled break-glass procedures.
