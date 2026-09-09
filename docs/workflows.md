# FoundryAI — Workflow Architecture

> **Status:** Updated 2026-09-08  
> **Implementation:** Runtime workflow engine is implemented and tested. Durable application-service integration remains future work.

## 1. Purpose

FoundryAI uses workflow-mediated multi-agent collaboration.

Agents do not freely chat or control the application.

## 2. Core Pattern

```text
Business Objective
  ↓
CEOPlanner
  ↓
ExecutionPlan
  ↓
WorkflowEngine
  ↓
Agent Tasks
  ↓
Specialist Agents
  ↓
Agent Results
  ↓
CEO synthesis
```

Future controlled execution:

```text
Agent Result
 ↓
Policy
 ↓
Approval where required
 ↓
Tool Execution
 ↓
Audit
```

## 3. Responsibility Model

### Workflow owns state

The workflow runtime owns execution ordering, dependency semantics, task status, and result collection.

### Agent owns reasoning

The agent decides how to reason about its assigned task.

### Tool owns execution

Tools perform deterministic application/external operations.

### Policy owns authorization

Future policy determines whether actions are allowed.

### Human owns consequential decisions

Future approvals handle high-impact actions.

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

`PlannedTask` contains:

```text
taskKey
agentType
objective
dependencies: List<String>
```

`ExecutionPlanBuilder` converts:

```text
taskKey → UUID
dependency taskKey → dependency UUID
```

This keeps model-facing planning identifiers separate from runtime IDs.

## 6. Dependency Semantics

A task can run only when all dependencies complete successfully.

Example:

```text
CFO
 ↓
Engineering
```

Dependencies answer:

> When may this task run?

They do not define how the agent reasons.

## 7. Context Semantics

`TaskContextBuilder` answers:

> What information should this task receive?

Current:

```text
AgentTask.context.data["dependencyResults"]
```

contains:

```text
Map<UUID, AgentResult>
```

Flow:

```text
dependency graph
      ↓
TaskDependencyResolver
      ↓
runnable task
      ↓
TaskContextBuilder
      ↓
dependency results
      ↓
AgentTask.context
```

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

If a task fails:

```text
Task → FAILED
dependent task → BLOCKED
```

Unknown dependencies are invalid workflow definitions.

Circular/no-progress graphs are rejected.

Runtime dispatcher exceptions become failed `AgentResult` values.

## 10. Current Execution Model

The engine is sequential.

Independent tasks are recognized by the graph but are not yet executed concurrently.

Parallel execution is deliberately deferred.

## 11. Parallelism — FUTURE

Potential future model:

```text
          ┌── CFO ─────────┐
CEO ──────┼── Engineering ├──→ synthesis
          └── Infrastructure┘
```

Potential controls:

```text
maximum workflow tasks
maximum tasks per agent type
maximum concurrent model calls
maximum tool operations
```

Never introduce unbounded parallel calls.

## 12. CEO Synthesis

Planning should produce specialist work only.

After execution:

```text
CFO result
Engineering result
Infrastructure result
        ↓
CEO synthesis
        ↓
Final recommendation
```

Do not use CFO as a generic final synthesis agent.

## 13. Re-planning

Future bounded re-planning:

```text
Initial plan
 ↓
specialist result
 ↓
material new information
 ↓
CEO re-plan
 ↓
new ExecutionPlan
```

Re-planning must be explicit and bounded.

## 14. Tool Failure

Future classification:

```text
timeout/provider outage/rate limit
    → retry if safe

invalid input/permission/business state
    → task failure

approval/security/destructive ambiguity
    → pause for human
```

Retries must be bounded.

## 15. Persistence

JPA foundation exists:

```text
Workflow
WorkflowTask
AgentExecution
```

Current runtime uses in-memory `ExecutionPlan` and `AgentTask` objects.

Next integration:

```text
persisted Workflow
 ↓
persisted WorkflowTask
 ↓
runtime AgentTask
 ↓
AgentExecution
 ↓
persist result/status
```

Do not hold long transactions open across external calls.

## 16. Cancellation

Future:

1. mark workflow cancelling,
2. prevent new tasks,
3. cancel cancellable work,
4. preserve history,
5. mark cancelled.

Completed external side effects cannot be magically rolled back.

## 17. Workflow Security

Future workflow execution should inherit authenticated authorization context.

Tool execution must re-check authorization because permissions can change after workflow creation.

## 18. Primary MVP Workflow

First reusable workflow shape:

```text
Product Launch / Cross-functional Planning
```

Future:

```text
Hiring Scenario
Infrastructure Change
```

## 19. Core Rule

> The workflow runtime is the controlled communication and state layer between agents.

No unrestricted agent-to-agent loops.
