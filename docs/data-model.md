# FoundryAI — Data Model

> **Status:** Updated 2026-09-08

## 1. Current JPA Entities

Exactly four:

```text
Request
Workflow
WorkflowTask
AgentExecution
```

Runtime/future concepts are not JPA entities:

```text
ExecutionPlan
PlannedTask
Agent
AgentTask
AgentContext
AgentResult
Tool
Approval
AuditEvent
User
Organization
```

## 2. Database

```text
MySQL
database: foundryai
```

Development:

```properties
spring.jpa.hibernate.ddl-auto=update
```

Production direction:

```text
Flyway
+
ddl-auto=validate
```

Flyway is not currently used.

## 3. Relationship

```text
Request
  1:1
Workflow
  1:N
WorkflowTask
  1:N
AgentExecution
```

## 4. Request

Represents the original business objective.

Current fields:

```text
UUID id
UUID userId
String objective
RequestStatus status
Instant createdAt
Instant updatedAt
```

Lifecycle:

```text
CREATED
PROCESSING
COMPLETED
FAILED
CANCELLED
```

## 5. Workflow

Represents durable execution associated with a request.

Current fields:

```text
UUID id
Request request
WorkflowStatus status
Instant createdAt
Instant updatedAt
List<WorkflowTask> tasks
```

Current lifecycle:

```text
CREATED
RUNNING
COMPLETED
FAILED
CANCELLED
```

Future statuses should only be added when actual workflow behavior needs them.

## 6. WorkflowTask

Represents durable logical work.

Conceptual fields:

```text
UUID id
Workflow workflow
AgentType agentType
String objective
TaskStatus status
Integer executionOrder
Instant startedAt
Instant completedAt
String result
String errorMessage
```

Runtime dependencies are currently represented on `AgentTask`.

Persistent dependency modeling should be introduced when the runtime is integrated with durable workflow persistence.

## 7. AgentExecution

Represents one actual execution attempt:

```text
WorkflowTask
  ├── Attempt 1 → FAILED
  ├── Attempt 2 → FAILED
  └── Attempt 3 → COMPLETED
```

Conceptual fields:

```text
UUID id
WorkflowTask workflowTask
AgentType agentType
Integer attempt
AgentExecutionStatus status
String input
String output
String errorMessage
String model
Instant startedAt
Instant completedAt
```

## 8. Runtime vs Persistence

```text
WorkflowTask
= durable logical work

AgentTask
= transient runtime instruction

AgentExecution
= durable execution attempt
```

## 9. ExecutionPlan

Runtime only:

```text
ExecutionPlan
  ├── AgentTask
  ├── AgentTask
  └── AgentTask
```

Planning-time:

```text
PlannedTask
  taskKey
  agentType
  objective
  dependencies: List<String>
```

Runtime conversion:

```text
ExecutionPlanBuilder
 ↓
AgentTask
 taskId: UUID
 dependencies: List<UUID>
```

## 10. Dependency Context

`TaskContextBuilder` creates:

```text
AgentContext.data["dependencyResults"]
```

Value:

```text
Map<UUID, AgentResult>
```

This is runtime data flow, not relational persistence.

## 11. AgentResult

Current:

```text
AgentType
AgentResultStatus
output
error
metadata
```

If exposed through REST, create a DTO.

## 12. Future Approval Model

Potential fields:

```text
workflowId
taskId
actionType
actionPayload
reason
riskLevel
status
requestedBy
approvedBy
requestedAt
expiresAt
decidedAt
decisionComment
```

Not current persistence.

## 13. Future Audit Model

Potential:

```text
workflowId
taskId
agentExecutionId
toolExecutionId
eventType
actorType
actorId
metadata
createdAt
correlationId
```

## 14. Future Tool Execution

Potential:

```text
workflowId
taskId
agentExecutionId
toolId/name
toolVersion
status
input metadata
output metadata
startedAt
completedAt
latency
retry count
error code
```

Do not automatically retain sensitive raw payloads.

## 15. Transactions

When external side effects are introduced:

```text
transaction
 ↓
record/validate state
 ↓
commit
 ↓
external action
 ↓
persist result
```

Never hold a long DB transaction while waiting for an external API.

## 16. Concurrency

Current runtime is sequential.

When concurrent workflow updates arrive, consider optimistic locking/versioning.

## 17. Multi-Tenancy

The MVP does not implement a full tenant model.

Do not add organization/user entities merely for architectural completeness.
