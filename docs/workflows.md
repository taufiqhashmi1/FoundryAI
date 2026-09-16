# FoundryAI — Workflow Architecture

> **Status:** Ground-truth update — 2026-09-16
> **Implementation:** Workflow runtime and application-service integration are implemented; advanced durability/parallelism remain future work.

## 1. Purpose

FoundryAI uses workflow-mediated multi-agent collaboration.

Agents do not freely chat and do not control application state directly.

## 2. Current Pattern

```text
Business Objective
      ↓
CEOPlanner
      ↓
ExecutionPlan
      ↓
WorkflowEngine
      ↓
AgentTask
      ↓
TaskContextBuilder
      ↓
WorkflowTaskDispatcher
      ↓
Specialist Agent
      ↓
AgentResult
      ↓
WorkflowService
      ↓
CEOSynthesizer
      ↓
CEORecommendationResponseDTO
```

## 3. Responsibility Model

### Workflow

Owns:

- task eligibility,
- ordering,
- dependency semantics,
- task status,
- result collection.

### Agent

Owns:

- domain reasoning,
- model interaction through `AiModelGateway`,
- returning an `AgentResult`.

### Tool

Owns:

- deterministic application operations,
- controlled external operations when implemented.

### Service

Owns:

- request/workflow lifecycle,
- persistence orchestration,
- connecting durable records to runtime execution,
- CEO synthesis at the application workflow level.

### Policy / Human

Future policy controls consequential actions; humans provide required approvals.

## 4. Current Components

```text
ExecutionPlan
PlannedTask
ExecutionPlanBuilder
TaskDependencyResolver
WorkflowTaskDispatcher
TaskContextBuilder
WorkflowEngine
WorkflowResult
WorkflowTaskStatus
```

## 5. Planning

`PlannedTask`:

```text
taskKey
agentType
objective
dependencies : List<String>
```

`ExecutionPlanBuilder` converts:

```text
taskKey → UUID
dependency taskKey → dependency UUID
```

This separates model-facing planning identifiers from runtime IDs.

## 6. Dependency Semantics

A task can run only when all dependencies have successfully completed.

```text
CFO
 ↓
Engineering
```

Dependencies answer:

> When may this task run?

They do not define how the agent reasons.

## 7. Context Semantics

`TaskContextBuilder` creates a fresh `AgentContext`.

Current structure:

```text
AgentTask.context.data["dependencyResults"]
```

The current implementation stores dependency results as:

```text
Map<UUID, Map<String,Object>>
```

where each nested result map contains:

```text
agentType
output
structuredData
assumptions
risks
```

Important current behaviors:

- if no dependencies exist, `dependencyResults` is still present as an empty map;
- missing dependency results are silently omitted;
- existing `AgentTask.context` is not preserved.

## 8. Dispatch

```text
Runnable AgentTask
       ↓
WorkflowTaskDispatcher
       ↓
AgentRegistry.getAgent(agentType)
       ↓
Agent.execute(task)
       ↓
AgentResult
```

The engine does not depend directly on concrete agent classes.

## 9. Failure Semantics

If a task returns a failure:

```text
task → FAILED
dependent task → BLOCKED
```

Unknown dependencies are rejected.

Circular/no-progress execution is rejected.

Dispatcher runtime exceptions are converted into `AgentResultStatus.FAILURE` rather than escaping as unhandled workflow failures.

## 10. Current Execution Model

The engine executes sequentially.

Independent tasks can be recognized as concurrently runnable, but the current implementation does not execute them concurrently.

Parallel execution is deferred.

## 11. CEO Synthesis

After specialist execution:

```text
CFO result
Engineering result
Infrastructure result
        ↓
CEOSynthesizer
        ↓
AiModelGateway.synthesize(...)
        ↓
CEORecommendationResponseDTO
```

Final synthesis is a CEO responsibility, not an arbitrary specialist task.

## 12. Re-Planning

Bounded re-planning is future work:

```text
Initial plan
 ↓
specialist result
 ↓
new material information
 ↓
CEO re-plan
 ↓
new bounded ExecutionPlan
```

## 13. Tool Failure

Sophisticated retry and failure classification are future work.

Current AI structured-output validation uses Spring AI's own schema-validation/self-correction mechanism.

That is separate from:

```text
network retry
provider outage retry
tool timeout retry
business-state retry
```

which are not comprehensively implemented.

## 14. Persistence Integration

The persistent hierarchy is:

```text
Request
 ↓
Workflow
 ↓
WorkflowTask
 ↓
AgentExecution
```

Application services connect durable workflow records to runtime execution.

The runtime itself still uses transient:

```text
ExecutionPlan
AgentTask
AgentContext
AgentResult
```

## 15. Cancellation

Cancellation API/workflow semantics are not a complete current capability. The target behavior is:

```text
mark cancelling
 ↓
prevent new work
 ↓
cancel cancellable work
 ↓
preserve history
 ↓
mark cancelled
```

External side effects cannot be assumed reversible.

## 16. Parallelism — Future

Potential future model:

```text
          ┌── CFO ─────────────┐
CEO ──────┼── Engineering ────┼──→ synthesis
          └── Infrastructure ──┘
```

Future execution must remain bounded by workflow/task/model/tool limits.
