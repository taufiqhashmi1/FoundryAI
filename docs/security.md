# FoundryAI — Security Architecture

> **Status:** Ground-truth update — 2026-09-16
> **Implementation:** Security principles and boundaries are defined. Complete authentication, authorization, policy enforcement, and approvals remain future work.

## 1. Security Principle

> **Never use the LLM as a security boundary.**

Model output can be wrong, manipulated, or malicious. Security decisions therefore belong to deterministic application code and future policy infrastructure.

## 2. Current Capability Boundary

Current tool capability selection is:

```text
AgentConfig
    ↓
tool names
    ↓
ToolRegistry
    ↓
ToolCallback
    ↓
Tool
```

This is not complete authorization.

## 3. Authentication

Not implemented.

Future authenticated context should be server-derived:

```text
userId
roles
organization / tenant
permissions
```

These values must not be trusted from arbitrary request JSON.

## 4. Authorization

Future authorization should determine:

```text
who is acting?
what action is requested?
which agent is acting?
which resource is affected?
which environment is affected?
does approval apply?
```

Authorization belongs on the server side.

## 5. Tool Permissions

The current agent configuration provides allowed tool names, but there is no complete permission/policy subsystem.

Target future classification:

```text
READ      → LOW
PROPOSE   → MEDIUM
EXECUTE   → HIGH
```

## 6. Policy

Future decisions:

```text
ALLOW
DENY
APPROVAL_REQUIRED
```

Potential inputs:

```text
agent
action
environment
risk
user permissions
approval state
security state
```

## 7. Human Approval

High-impact operations should eventually require explicit approval, such as:

- production deployment,
- protected-code merge,
- material budget modifications,
- material purchases,
- destructive infrastructure changes.

The current MVP does not autonomously execute these real-world actions.

## 8. Secrets

Secrets must never be:

- embedded in prompts,
- committed to source,
- returned by the model,
- written to ordinary logs.

Provider API keys are supplied through configuration/environment rather than model prompts.

## 9. Prompt Injection

External content must be treated as data, not trusted instructions:

```text
repositories
issues
documents
vendor records
uploaded files
web content
```

The model does not get to redefine application policy through external text.

## 10. Database Security

Agents do not receive arbitrary SQL execution capability.

Database operations remain in application code/repositories.

## 11. Multi-Tenancy

Full tenant isolation is not implemented in the MVP.

Future tenant isolation belongs in authenticated service/repository boundaries and authorization policy.

## 12. Security Maturity Boundary

The current system should be described as an agentic application MVP with security-aware architectural boundaries, not as a production-grade autonomous security platform.
