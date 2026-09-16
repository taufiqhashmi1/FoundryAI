# FoundryAI

**FoundryAI — Agentic AI Operating System for a FinTech Startup**

FoundryAI is a Java/Spring Boot application that accepts a business objective, uses a CEO planner to decompose the objective into specialist work, executes the work through a dependency-aware workflow engine, and produces a CEO-level recommendation from the specialist results.

The project is deliberately implemented as a **single Spring Boot application** rather than a distributed multi-agent platform.

## Current MVP

Four agent types are implemented:

```text
CEO / Orchestrator
CFO
Engineering
Infrastructure
```

Current core capabilities:

```text
structured CEO planning
specialist execution
dependency-aware workflow execution
dependency-result context propagation
deterministic financial tool calling
CEO synthesis
REST workflow execution
MySQL persistence
```

## Technology

```text
Java 25
Spring Boot 4.1.1
Spring AI 2.0.1
Maven
MySQL 8.0.40
Hibernate ORM 7.4.5.Final
Groq
```

## Architecture

```text
Client
  ↓
RequestController
  ↓
RequestService
  ↓
WorkflowService
  ↓
CEOPlanner
  ↓
AiModelGateway
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
CEOSynthesizer
  ↓
CEORecommendationResponseDTO
```

Core design rule:

```text
CEO       → WHAT
Workflow  → WHEN / WHETHER
Agent     → HOW
Tool      → HOW a deterministic operation is performed
```

## AI

Groq is accessed through Spring AI's OpenAI-compatible integration.

Structured outputs use Spring AI's `.entity(...)` API with `.validateSchema()` rather than manual JSON parsing.

## Tools

The current deterministic tool is:

```text
financial-calculator
```

It calculates:

```text
monthly cost × number of months
```

with `BigDecimal` and input validation.

## Persistence

Current JPA entities:

```text
Request
Workflow
WorkflowTask
AgentExecution
```

## API

A demonstrated execution endpoint is:

```http
POST /api/workflows/requests/{requestId}/execute
```

A local Postman test has returned a `COMPLETED` workflow with successful specialist tasks and a CEO recommendation.

## Testing

The repository contains unit and integration tests for the runtime, tools, planning, context propagation, and CEO end-to-end behavior.

The most recent supplied full Maven run before test alignment reported 57 tests with 10 failures and 1 error. The failing tests were then revised to match the current `TaskContextBuilder` and `WorkflowEngine` contracts. A fresh full-suite run should be used for the final verification.

## Not implemented

The following are future capabilities:

```text
authentication/authorization
policy engine
human approval
audit event persistence
parallel workflow execution
distributed workers
external production integrations
persistent CoALA memory
full production hardening
```

## Documentation

See:

```text
vision.md
requirements.md
architecture.md
agents.md
ai-design.md
tools.md
workflows.md
data-model.md
mvp-plan.md
progress.md
apis.md
security.md
governance.md
evaluation.md
file-structure.md
CHANGELOG.md
```
