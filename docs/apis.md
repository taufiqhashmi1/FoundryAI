# FoundryAI — API Design

> **Status:** Updated 2026-09-08  
> **REST implementation:** Not yet implemented.

## 1. Purpose

The API should expose application resources, not concrete agent implementation details.

The runtime currently exists beneath the future REST layer.

## 2. Principles

- RESTful resource design
- asynchronous workflow execution
- typed request/response models
- explicit errors
- idempotency for side effects
- authentication/authorization
- correlation IDs
- versioned APIs

Base path:

```text
/api/v1
```

## 3. Controllers

Planned:

```text
RequestController
WorkflowController
ApprovalController
```

Never create per-agent controllers.

## 4. Request API

```http
POST /api/v1/requests
GET  /api/v1/requests/{requestId}
```

Example:

```json
{
  "objective": "Launch reconciliation in 8 weeks within our financial constraints."
}
```

The eventual create operation should return quickly and allow the workflow to run asynchronously.

Conceptual response:

```http
202 Accepted
```

```json
{
  "requestId": "...",
  "workflowId": "...",
  "status": "PROCESSING"
}
```

## 5. Workflow API

```http
GET  /api/v1/workflows/{workflowId}
GET  /api/v1/workflows/{workflowId}/tasks
POST /api/v1/workflows/{workflowId}/cancel
```

The API exposes durable workflow state without leaking runtime implementation details.

## 6. Plan API

Future:

```http
GET /api/v1/workflows/{workflowId}/plan
```

Map `ExecutionPlan` to a DTO. Never expose the runtime object directly.

## 7. Approval API

Future:

```http
GET  /api/v1/approvals?status=PENDING
GET  /api/v1/approvals/{approvalId}
POST /api/v1/approvals/{approvalId}/approve
POST /api/v1/approvals/{approvalId}/reject
```

Server-side authorization must be re-checked.

## 8. Audit API

Future:

```http
GET /api/v1/workflows/{workflowId}/events
```

Potential events:

```text
REQUEST_CREATED
PLAN_CREATED
TASK_STARTED
AGENT_STARTED
TOOL_INVOKED
TOOL_COMPLETED
APPROVAL_REQUIRED
APPROVAL_GRANTED
ACTION_EXECUTED
WORKFLOW_COMPLETED
WORKFLOW_FAILED
```

## 9. DTO Boundary

Current DTOs:

```text
CreateRequestDTO
ErrorResponseDTO
RequestResponseDTO
ValidationErrorResponseDTO
WorkflowResponseDTO
WorkflowTaskResponseDTO
```

Runtime objects remain internal:

```text
AgentTask
AgentContext
AgentResult
PlannedTask
ExecutionPlan
```

Anything crossing REST gets a DTO.

## 10. Authentication Boundary

Authentication is future work.

The server should derive:

```text
userId
roles
organization/tenant
permissions
```

Never trust those values from JSON.

## 11. HTTP Statuses

```text
200 OK
201 Created
202 Accepted
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
409 Conflict
422 Unprocessable Entity
429 Too Many Requests
500 Internal Server Error
503 Service Unavailable
```

## 12. Idempotency

Eventually support:

```http
Idempotency-Key: <key>
```

for workflow creation, approval actions, and external side-effecting tools.

## 13. Correlation IDs

Future propagation:

```text
request
 ↓
workflow
 ↓
agent execution
 ↓
tool execution
 ↓
logs
 ↓
audit
```

## 14. API Evolution

Prefer:

```http
POST /requests
GET /workflows/{id}
```

over:

```http
POST /ceo-agent/run
```

This keeps the public API independent of internal agent implementation.

## 15. Initial Public Surface

```text
POST   /api/v1/requests
GET    /api/v1/requests/{id}
GET    /api/v1/workflows/{id}
GET    /api/v1/workflows/{id}/tasks
POST   /api/v1/workflows/{id}/cancel
```
