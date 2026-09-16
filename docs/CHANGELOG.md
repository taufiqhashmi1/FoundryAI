# FoundryAI Documentation Changelog

## 2026-09-16 — Ground-Truth Update

The documentation was reconciled with the implementation and development work completed after the 2026-09-08 documentation baseline.

### Major implementation changes reflected

```text
AiModelGateway became the single AI gateway
ModelRouter retained as AgentType → AgentSettings router

CEOPlanner implemented
CEOSynthesizer implemented
CEORecommendationResponseDTO introduced

Spring AI structured output migrated to .entity(...)
Spring AI validateSchema() added for structured-output validation/self-correction

Manual LLM JSON parsing removed for structured outputs
ObjectMapper retained only where needed for context serialization

WorkflowService / RequestService integrated with persistence
REST request/workflow execution path implemented

WorkflowEngine context builder integrated
TaskContextBuilder currently serializes dependency AgentResult objects
into Map<String,Object> values

Workflow persistence includes:
Request
Workflow
WorkflowTask
AgentExecution
```

### Current tool architecture

```text
AgentConfig
    ↓
allowed tool names
    ↓
ToolRegistry
    ↓
ToolCallback
    ↓
deterministic tool
```

There is no `AgentToolResolver`.

### Current workflow behavior

```text
plan validation
dependency validation
runnable-task detection
dependency blocking
sequential dispatch
context propagation
result collection
dispatcher-exception conversion
cycle/no-progress detection
```

### Current CEO synthesis flow

```text
specialist AgentResult map
        ↓
CEOSynthesizer
        ↓
AiModelGateway.synthesize(...)
        ↓
Spring AI entity + schema validation
        ↓
CEORecommendationResponseDTO
```

### Documentation corrections

The following obsolete statements were removed:

- `StructuredAiModelGateway` as the current gateway abstraction.
- CEO synthesis as future-only.
- REST/services as entirely unimplemented.
- runtime/persistence integration as entirely future.
- dependency context as `Map<UUID, AgentResult>`; the implementation currently exposes serialized result maps under `dependencyResults`.
- claims that all tests were passing.

### Verification status

A supplied full Maven test run executed 57 tests and reported 10 failures plus 1 error, concentrated in `TaskContextBuilderTest`, `WorkflowEngineTest`, and `WorkflowEngineContextIntegrationTest`. The failing tests were then aligned with the current `TaskContextBuilder` contract. A new complete Maven run after those test changes has not yet been established in this documentation update.

### Known current limitation

The MVP does not yet implement:

```text
authentication/authorization
policy engine
human approval
audit event persistence
parallel workflow execution
external production integrations
persistent CoALA memory
distributed workers
```

These remain separate future capabilities rather than current features.
