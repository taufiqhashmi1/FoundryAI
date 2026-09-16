# FoundryAI — API Design

> **Status:** Ground-truth update — 2026-09-16
> **REST:** Implemented for the current MVP workflow/request path.

## 1. Purpose

The REST layer exposes application resources and workflow outcomes. Concrete agent implementation classes are not API resources.

## 2. Current Controller Layer

```text
controllers/
├── RequestController.java
├── WorkflowController.java
└── ApprovalController.java
```

`ApprovalController` exists as a future-facing boundary; approval enforcement itself is not implemented.

## 3. Current Request Flow

The current implementation supports request creation/retrieval and workflow execution through the service layer.

The demonstrated execution endpoint is:

```http
POST /api/workflows/requests/{requestId}/execute
```

The endpoint executes the workflow for an existing request and returns the resulting workflow representation.

The current working Postman test returned:

```text
status: COMPLETED
recommendation: populated
tasks: populated
```

with successful CFO, Engineering, and Infrastructure execution.

## 4. Request DTO Boundary

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

The REST layer does not expose runtime `AgentTask`, `AgentContext`, or `ExecutionPlan` objects directly.

`CreateRequestDTO` validates the request objective using Jakarta validation annotations.

## 5. Workflow Response

The current `WorkflowResponseDTO` represents:

```text
workflow id
request id
workflow status
createdAt
updatedAt
tasks
CEO recommendation
```

The recommendation is represented by:

```text
CEORecommendationResponseDTO
```

with:

```text
recommendation
keyFindings
assumptions
risks
nextSteps
```

## 6. Workflow Task Response

A workflow task response contains:

```text
id
agentType
objective
status
executionOrder
startedAt
completedAt
result
errorMessage
```

## 7. Error Handling

Application exceptions include:

```text
BadRequestException
ResourceNotFoundException
```

`GlobalExceptionHandler` translates application and validation errors into typed error DTOs.

## 8. API Design Direction

The broader target remains resource-oriented and versionable, with future support for:

```text
request retrieval
workflow retrieval
task retrieval
workflow cancellation
plan retrieval
approval operations
audit/event retrieval
```

The current code should be treated as the source of truth for which endpoints are actually exposed; future endpoint lists are design direction, not implemented capability.

## 9. Authentication

Authentication and server-side authorization are not implemented in the current MVP.

User/role/tenant identity must not be trusted from arbitrary JSON fields when authentication is introduced.
