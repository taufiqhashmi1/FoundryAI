# FoundryAI — Data Model

> **Status:** Ground-truth update — 2026-09-16

## 1. Current JPA Entities

Exactly four JPA entities are currently defined:

```text
Request
Workflow
WorkflowTask
AgentExecution
```

The following are runtime/application concepts, not JPA entities:

```text
ExecutionPlan
PlannedTask
Agent
AgentTask
AgentContext
AgentResult
StructuredAgentResponse
Tool
```

Future concepts such as approval, audit event, user, and organization are not current JPA entities.

## 2. Database

```text
MySQL
database: foundryai
```

Development uses:

```properties
spring.jpa.hibernate.ddl-auto=update
```

Flyway is not currently used.

## 3. Entity Relationships

```text
Request
  1 : 1
Workflow
  1 : N
WorkflowTask
  1 : N
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
String recommendation
List<WorkflowTask> tasks
```

Lifecycle:

```text
CREATED
RUNNING
COMPLETED
FAILED
CANCELLED
```

The current workflow can persist the final CEO recommendation in the workflow record.

## 6. WorkflowTask

Represents durable logical work associated with a workflow.

Current fields:

```text
UUID id
Workflow workflow
String agentType
String objective
TaskStatus status
Integer executionOrder
Instant startedAt
Instant completedAt
String result
String errorMessage
```

The persisted `agentType` is currently represented as a string column.

Runtime dependency UUIDs live on `AgentTask`; persistent dependency relationships are not separately modeled as a dedicated JPA dependency table.

## 7. AgentExecution

Represents one durable execution attempt associated with a workflow task.

Current fields:

```text
UUID id
WorkflowTask workflowTask
String agentType
Integer attempt
AgentExecutionStatus status
String input
String output
String model
String errorMessage
Instant startedAt
Instant completedAt
```

The current entity supports attempt numbering even though sophisticated retry policies are not yet implemented.

## 8. Runtime vs Persistence

```text
WorkflowTask
= durable logical task

AgentTask
= transient runtime instruction

AgentExecution
= durable execution attempt
```

## 9. Execution Plan

`ExecutionPlan` is runtime-only:

```text
ExecutionPlan
  ├── AgentTask
  ├── AgentTask
  └── AgentTask
```

`PlannedTask` is the model-facing planning representation:

```text
taskKey
agentType
objective
dependencies
```

`ExecutionPlanBuilder` translates model-facing task keys into runtime UUIDs.

## 10. DTO Representation

REST clients do not receive JPA entities directly.

Current DTOs include:

```text
CreateRequestDTO
RequestResponseDTO
WorkflowResponseDTO
WorkflowTaskResponseDTO
CEORecommendationResponseDTO
ErrorResponseDTO
ValidationErrorResponseDTO
```

## 11. Future Data Model

When required, the system can add dedicated persistence for:

```text
PolicyDecision
Approval
AuditEvent
ToolExecution
User / Organization
Memory records
```

These are not present in the current MVP data model.
