# FoundryAI — System Architecture

> **Status:** Ground-truth update — 2026-09-16
> **Current architecture:** Single Spring Boot application with hierarchical orchestration, workflow-mediated execution, Groq-backed Spring AI integration, deterministic tools, persistence-backed workflow services, and REST endpoints.

## 1. Goal

FoundryAI is an agentic application platform, not a monolithic chatbot and not a distributed microservice deployment.

Current deployment shape:

```text
One Spring Boot application
        +
MySQL
        +
Groq / Spring AI
```

## 2. Technology

```text
Java 25
Spring Boot 4.1.1
Spring AI 2.0.1
Maven
MySQL 8.0.40
Hibernate ORM 7.4.5.Final
Spring MVC
Jakarta Validation
Actuator
Groq
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

These plural package names are intentional and must be preserved.

## 4. End-to-End Implemented Flow

```text
Client
  ↓
RequestController
  ↓
RequestService
  ↓
Request / Workflow persistence
  ↓
WorkflowService
  ↓
CEOPlanner
  ↓
AiModelGateway
  ↓
Spring AI / Groq
  ↓
ExecutionPlan
  ↓
WorkflowEngine
  ↓
TaskDependencyResolver
  ↓
TaskContextBuilder
  ↓
WorkflowTaskDispatcher
  ↓
AgentRegistry
  ├── CFOAgent
  ├── EngineeringAgent
  └── InfrastructureAgent
  ↓
AgentResult
  ↓
WorkflowService
  ↓
CEOSynthesizer
  ↓
AiModelGateway
  ↓
CEORecommendationResponseDTO
  ↓
Workflow persistence / response DTO
  ↓
Client
```

The CEO agent class also exists, but current workflow planning/synthesis uses `CEOPlanner` and `CEOSynthesizer` rather than dispatching a CEO `AgentTask` as the specialist work item.

## 5. Core Responsibility Rule

```text
CEO       → WHAT
Workflow  → WHEN / WHETHER
Agent     → HOW
Tool      → HOW a deterministic/external operation is performed
Service   → application lifecycle + persistence orchestration
```

The CEO does not directly call concrete specialist agents.

## 6. Runtime vs Persistence

Runtime concepts:

```text
Agent
AgentTask
AgentContext
AgentResult
ExecutionPlan
PlannedTask
WorkflowEngine
WorkflowResult
```

Persisted concepts:

```text
Request
Workflow
WorkflowTask
AgentExecution
```

The distinction is deliberate.

## 7. Workflow Engine

The current workflow engine:

- validates the execution plan,
- validates task IDs/dependencies,
- finds runnable tasks,
- blocks tasks whose dependencies failed,
- builds dependency context,
- dispatches through `AgentRegistry`,
- collects `AgentResult`,
- converts dispatcher runtime exceptions to failed results,
- detects cycles/no-progress conditions.

Execution is currently sequential.

## 8. Persistence Integration

`RequestService` and `WorkflowService` connect application requests to persisted workflow records.

Current persisted hierarchy:

```text
Request
  1:1
Workflow
  1:N
WorkflowTask
  1:N
AgentExecution
```

The current implementation persists workflow/task/execution information while executing the request flow.

## 9. AI Path

```text
Concrete Agent / CEO planner / CEO synthesizer
       ↓
AiModelGateway
       ↓
ModelRouter
       ↓
AgentConfig.AgentSettings
       ↓
ChatClient
       ↓
Groq
```

## 10. Tool Path

```text
AiModelGatewayImpl
       ↓
AgentSettings.tools
       ↓
ToolRegistry
       ↓
ToolCallback
       ↓
FinancialCalculatorTool
```

## 11. Security Boundary

Authentication, authorization, policy decisions, human approvals, and durable audit infrastructure remain future capabilities.

The current tool list is therefore a capability configuration mechanism, not a complete authorization system.

## 12. Scaling Boundary

The MVP is not a distributed worker platform.

Future scaling can introduce:

- asynchronous workers,
- queue/event infrastructure,
- bounded parallel execution,
- tenant isolation,
- stronger observability,

without changing the core agent contract.
