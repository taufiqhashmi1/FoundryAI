# FoundryAI — Project Progress & Ground-Truth Handoff

> **Canonical project state as of 2026-09-16**

## 1. Project

**FoundryAI — Agentic AI Operating System for a FinTech Startup**

Base package:

```text
com.taufiqhashmi.foundryai
```

Goal:

> Build a minimal, scalable, extensible agentic-AI application that translates business objectives into controlled specialist workflows and produces a final CEO-level recommendation.

The current application is a single Spring Boot service, not a distributed autonomous company platform.

## 2. Technology

```text
Java 25
Spring Boot 4.1.1
Spring AI 2.0.1
Maven
MySQL 8.0.40
Hibernate ORM 7.4.5.Final
Groq
```

Development database:

```text
jdbc:mysql://localhost:3306/foundryai
```

## 3. Exact Package Structure

```text
com.taufiqhashmi.foundryai
├── agents
├── ai
├── controllers
├── dtos
├── entities
├── exceptions
├── repositories
├── services
├── tools
└── workflows
```

## 4. Foundation

Implemented JPA entities:

```text
Request
Workflow
WorkflowTask
AgentExecution
```

Repositories:

```text
RequestRepository
WorkflowRepository
WorkflowTaskRepository
AgentExecutionRepository
```

DTOs:

```text
CreateRequestDTO
RequestResponseDTO
WorkflowResponseDTO
WorkflowTaskResponseDTO
CEORecommendationResponseDTO
ErrorResponseDTO
ValidationErrorResponseDTO
```

Exceptions:

```text
BadRequestException
ResourceNotFoundException
GlobalExceptionHandler
```

## 5. Agent Runtime

Implemented:

```text
Agent
AgentType
AgentTask
AgentContext
AgentResult
AgentResultStatus
StructuredAgentResponse
AgentConfig
AgentRegistry
```

Types:

```text
CEO
CFO
ENGINEERING
INFRASTRUCTURE
```

Result statuses:

```text
SUCCESS
FAILURE
```

Concrete implementations:

```text
CEOAgent
CFOAgent
EngineeringAgent
InfrastructureAgent
```

## 6. AI Runtime

Implemented:

```text
AiModelGateway
AiModelGatewayImpl
ModelRouter
```

Provider path:

```text
Agent
 ↓
AiModelGateway
 ↓
ModelRouter
 ↓
AgentSettings
 ↓
Spring AI ChatClient
 ↓
Groq
```

Agent-specific configuration contains:

```text
model
systemPrompt
temperature
maxTokens
tools
includeReasoning
```

## 7. Structured AI

Implemented:

```text
CEOPlanner
CEOSynthesizer
PlannedTask
ExecutionPlan
ExecutionPlanBuilder
CEORecommendationResponseDTO
```

Important architectural correction:

> There is no longer a separate `StructuredAiModelGateway` in the implementation. Structured planning and synthesis are exposed through the unified `AiModelGateway`.

## 8. Structured Output

`AiModelGatewayImpl` uses Spring AI `.entity(...)` for structured outputs and `.validateSchema()` for schema validation/self-correction.

This replaced manual `ObjectMapper.readValue(...)` parsing of model responses.

`ObjectMapper` is still used where application context needs serialization into a prompt.

## 9. Tool Runtime

Implemented:

```text
ToolRegistry
ToolConfiguration
FinancialCalculatorTool
```

Current tool:

```text
financial-calculator
```

Behavior:

```text
monthlyCost × months
```

The tool uses `BigDecimal` and validates null/negative monthly cost and non-positive month counts.

## 10. Workflow Runtime

Implemented:

```text
ExecutionPlan
PlannedTask
ExecutionPlanBuilder
TaskDependencyResolver
TaskContextBuilder
WorkflowTaskDispatcher
WorkflowEngine
WorkflowResult
WorkflowTaskStatus
```

Current execution model:

```text
sequential
dependency-aware
in-memory runtime
```

Behavior includes:

```text
plan validation
dependency validation
runnable-task detection
dependency blocking
context construction
agent dispatch
result collection
dispatcher exception conversion
cycle/no-progress detection
```

## 11. Context Propagation

The current `TaskContextBuilder` places:

```text
context.data["dependencyResults"]
```

where each dependency result is converted from `AgentResult` into a `Map<String,Object>` containing:

```text
agentType
output
structuredData
assumptions
risks
```

It currently:

- creates a fresh context,
- always creates `dependencyResults`,
- silently ignores missing dependency results,
- does not preserve an existing `AgentTask.context`.

This is the current implementation contract.

## 12. Services and Persistence

Implemented:

```text
RequestService
WorkflowService
```

The service layer connects request/workflow persistence to runtime planning, execution, task persistence, agent execution records, and CEO synthesis.

## 13. REST

Implemented controller boundaries:

```text
RequestController
WorkflowController
```

Approval controller exists as a boundary but approval processing is not implemented.

A local Postman execution has produced a completed workflow containing:

```text
CFO = COMPLETED
ENGINEERING = COMPLETED
INFRASTRUCTURE = COMPLETED
CEO recommendation = populated
workflow status = COMPLETED
```

## 14. Verification

The latest supplied full Maven run before test updates:

```text
57 tests
10 failures
1 error
```

The failures were concentrated in:

```text
TaskContextBuilderTest
WorkflowEngineTest
WorkflowEngineContextIntegrationTest
```

The test code was subsequently aligned with the current TaskContextBuilder contract and WorkflowEngine construction. A fresh full Maven result after those changes is still required before claiming a clean suite.

## 15. Deferred Work

```text
authentication
authorization
policy engine
human approvals
audit persistence
parallelism
distributed workers
external production tools/integrations
persistent CoALA memory
production hardening
```
