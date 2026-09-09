# FoundryAI — System Architecture

> **Status:** Updated 2026-09-08  
> **Current architecture:** Single Spring Boot application with hierarchical orchestration, structured planning, workflow-mediated execution, and deterministic tool callbacks.

## 1. Goal

FoundryAI is a modular agentic platform, not a monolithic chatbot and not yet a distributed microservice platform.

MVP deployment:

```text
One Spring Boot application
+
MySQL
+
LLM provider
```

## 2. Technology

```text
Java 25
Spring Boot 4.1.1
Spring AI 2.0.1
Maven
MySQL
JPA / Hibernate
Spring MVC
Validation
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

These plural package names are intentional.

## 4. Current Logical Flow

```text
Business Objective
       ↓
CEOPlanner
       ↓
StructuredAiModelGateway
       ↓
PlannedTask[]
       ↓
ExecutionPlanBuilder
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
```

AI path:

```text
Concrete Agent
 ↓
AiModelGateway
 ↓
ModelRouter
 ↓
Spring AI
 ↓
Groq
```

Tool path:

```text
AiModelGateway
 ↓
ToolRegistry
 ↓
ToolCallback
 ↓
Tool implementation
```

## 5. Core Rule

```text
CEO decides WHAT.
Workflow decides WHEN / WHETHER.
Agent decides HOW.
Tool decides HOW external/system action is performed.
```

The CEO must not directly depend on:

```java
cfoAgent.execute();
engineeringAgent.execute();
```

## 6. Implementation Status

Implemented:

```text
Agent runtime
four concrete agents
AI gateway
model router
structured planning gateway
CEOPlanner
execution plan builder
tool registry
financial calculator
workflow engine
dependency resolver
task dispatcher
context builder
```

Existing persistence foundation:

```text
Request
Workflow
WorkflowTask
AgentExecution
```

Not yet connected end-to-end:

```text
RequestService
WorkflowService
REST
JPA workflow persistence ↔ runtime engine
```

Future:

```text
CEO synthesis
policy
approval
audit
authentication
parallelism
retries/timeouts
production integrations
CoALA memory
```

## 7. Persistence vs Runtime

JPA:

```text
Request
Workflow
WorkflowTask
AgentExecution
```

Runtime:

```text
Agent
AgentTask
AgentContext
AgentResult
PlannedTask
ExecutionPlan
CEOPlanner
AgentRegistry
AiModelGateway
StructuredAiModelGateway
ModelRouter
ToolRegistry
WorkflowEngine
```

Do not make every runtime concept a database entity.

## 8. Workflow Semantics

Current engine:

- validates task IDs,
- validates dependencies,
- detects cycles/no-progress,
- finds runnable tasks,
- blocks dependents after failure,
- builds context,
- dispatches agents,
- collects results.

Current execution is sequential.

The dependency graph is intentionally designed for future parallel execution.

## 9. Context Propagation

```text
CFO AgentResult
      ↓
TaskContextBuilder
      ↓
Engineering AgentTask.context
      ↓
Engineering Agent
```

Current key:

```text
dependencyResults
```

Current value:

```text
Map<UUID, AgentResult>
```

## 10. CEO Synthesis

Current planning ends at specialist tasks.

Next:

```text
CFO result
Engineering result
Infrastructure result
        ↓
CEO synthesis
        ↓
Final business outcome
```

Do not delegate generic final synthesis to CFO.

## 11. Tool Boundary

Current:

```text
Agent
 ↓
AiModelGateway
 ↓
ToolRegistry
 ↓
ToolCallback
 ↓
Tool implementation
```

Future:

```text
tool request
 ↓
capability
 ↓
policy
 ↓
approval
 ↓
external adapter
```

## 12. Security Boundary

Future:

```text
Authentication
 ↓
User authorization
 ↓
Workflow authorization
 ↓
Agent permissions
 ↓
Tool permissions
 ↓
Policy
 ↓
Approval
 ↓
Execution
```

The current MVP does not implement these enforcement layers.

## 13. Scalability

Current:

```text
Single application
```

Future:

```text
API instances
+
workflow workers
+
agent workers
+
durable messaging
+
MySQL
```

Do not introduce distributed infrastructure before the core runtime is stable.

## 14. Deployment

Kubernetes, Kafka, Redis, and microservices remain deferred.

## 15. Extensibility

Adding an agent requires:

1. implementation,
2. `AgentType`,
3. configuration,
4. tools,
5. tests.

Adding a tool requires:

1. implementation,
2. registry registration,
3. configuration,
4. future policy classification,
5. tests.

Core orchestration should remain unchanged.
