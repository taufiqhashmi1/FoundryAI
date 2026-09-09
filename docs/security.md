# FoundryAI — Security Architecture

> **Status:** Updated 2026-09-08  
> **Implementation:** Security architecture is defined; production authentication/authorization/policy enforcement are future work.

## 1. Security Goal

FoundryAI must assume model output can be incorrect, manipulated, or malicious.

> **Never use the LLM as a security boundary.**

Security decisions are deterministic application responsibilities.

## 2. Defense in Depth

Future:

```text
Identity
 ↓
API Authorization
 ↓
Workflow Authorization
 ↓
Agent Permissions
 ↓
Tool Permissions
 ↓
Policy
 ↓
Approval
 ↓
Execution
```

## 3. Current Capability Boundary

Current tool capability selection:

```text
AgentConfig
 ↓
tool names
 ↓
ToolRegistry
 ↓
ToolCallback
```

This is not complete authorization.

`ToolRegistry` must remain a registry, not a policy engine.

## 4. Authentication

Not implemented.

Future authenticated context:

```text
userId
roles
organization/tenant
permissions
```

Never trust these values from request JSON.

## 5. Authorization

Future authorization must determine:

```text
Who is acting?
What are they trying to do?
Which agent is acting?
Which resource is affected?
Which environment is affected?
Does approval apply?
```

Authorization is server-side.

## 6. Agent Permissions

Agents receive explicit capabilities.

Example:

```text
CFO
 ├── financial READ
 ├── financial CALCULATE
 └── financial PROPOSE

CFO
 └── production deployment DENIED
```

CEO does not inherit every specialist capability.

## 7. Tool Permissions

Future classifications:

```text
getCashBalance
→ READ / LOW

createPullRequest
→ PROPOSE / MEDIUM

mergePullRequest
→ EXECUTE / HIGH

deployProduction
→ EXECUTE / HIGH
```

## 8. Policy

Future decisions:

```text
ALLOW
DENY
APPROVAL_REQUIRED
```

Conceptual chain:

```text
tool request
 ↓
agent permission
 ↓
user permission
 ↓
environment
 ↓
risk
 ↓
security state
 ↓
approval
```

## 9. Human Approval

High-risk operations should require explicit approval:

- protected production merge,
- production deployment,
- material budget modification,
- material purchase,
- destructive infrastructure changes.

Approval must bind to exact action parameters.

## 10. Secrets

Secrets must never be:

- embedded in prompts,
- committed to source,
- returned by the model,
- written to ordinary logs,
- stored in vector memory.

The model must never receive raw provider tokens.

## 11. Prompt Injection

Treat external content as untrusted:

```text
repository content
issues
documents
vendor records
uploaded files
web content
```

Instructions contained in those sources are data, not authority.

## 12. Data Isolation

The MVP does not implement full multi-tenancy.

Future tenant/resource isolation belongs at authorization and service/repository boundaries.

## 13. Database Security

Agents must not execute arbitrary SQL.

They interact through application tools/services.

## 14. Code Execution

If Engineering eventually executes code/tests:

- isolate execution,
- restrict filesystem,
- restrict network,
- enforce CPU/memory/time limits,
- never expose production credentials,
- destroy temporary environments.

## 15. Infrastructure Security

Preferred future flow:

```text
PLAN
 ↓
VALIDATE
 ↓
SECURITY REVIEW
 ↓
PR
 ↓
HUMAN APPROVAL
 ↓
APPLY
```

Never let an LLM directly execute arbitrary production commands.

## 16. Logging

Useful metadata:

```text
correlationId
workflowId
taskId
agent type
toolExecutionId
event type
status
latency
```

Do not log secrets, credentials, unnecessary personal data, sensitive financial records, or sensitive raw prompts.

## 17. Threat Model

```text
T1 Prompt injection
T2 Tool abuse
T3 Agent loops
T4 Credential leakage
T5 Cross-tenant access
T6 Unsafe production action
T7 Malicious repository/document content
```

Mitigations include allowlists, policy, approvals, sandboxing, credential isolation, and bounded execution.

## 18. Security Priority

When implementation begins:

1. authentication,
2. authorization,
3. agent/tool allowlists,
4. policy,
5. approval,
6. secret handling,
7. audit,
8. sandboxing,
9. prompt-injection controls,
10. tenant/resource isolation.
