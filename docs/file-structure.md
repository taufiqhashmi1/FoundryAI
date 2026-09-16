# FoundryAI — Project File Structure

> **Status:** Ground-truth update — 2026-09-16

```text
com.taufiqhashmi.foundryai
│
├── agents/
│   ├── Agent.java
│   ├── AgentConfig.java
│   ├── AgentContext.java
│   ├── AgentRegistry.java
│   ├── AgentResult.java
│   ├── AgentResultStatus.java
│   ├── AgentTask.java
│   ├── AgentType.java
│   ├── StructuredAgentResponse.java
│   ├── CEOPlanner.java
│   ├── CEOSynthesizer.java
│   └── implementations/
│       ├── CEOAgent.java
│       ├── CFOAgent.java
│       ├── EngineeringAgent.java
│       └── InfrastructureAgent.java
│
├── ai/
│   ├── AiModelGateway.java
│   ├── AiModelGatewayImpl.java
│   └── ModelRouter.java
│
├── controllers/
│   ├── RequestController.java
│   ├── WorkflowController.java
│   └── ApprovalController.java
│
├── dtos/
│   ├── CreateRequestDTO.java
│   ├── ErrorResponseDTO.java
│   ├── RequestResponseDTO.java
│   ├── ValidationErrorResponseDTO.java
│   ├── WorkflowResponseDTO.java
│   ├── WorkflowTaskResponseDTO.java
│   └── CEORecommendationResponseDTO.java
│
├── entities/
│   ├── Request.java
│   ├── RequestStatus.java
│   ├── Workflow.java
│   ├── WorkflowStatus.java
│   ├── WorkflowTask.java
│   ├── TaskStatus.java
│   ├── AgentExecution.java
│   └── AgentExecutionStatus.java
│
├── exceptions/
│   ├── BadRequestException.java
│   ├── ResourceNotFoundException.java
│   └── GlobalExceptionHandler.java
│
├── repositories/
│   ├── RequestRepository.java
│   ├── WorkflowRepository.java
│   ├── WorkflowTaskRepository.java
│   └── AgentExecutionRepository.java
│
├── services/
│   ├── RequestService.java
│   └── WorkflowService.java
│
├── tools/
│   ├── FinancialCalculatorTool.java
│   ├── ToolConfiguration.java
│   └── ToolRegistry.java
│
└── workflows/
    ├── ExecutionPlan.java
    ├── PlannedTask.java
    ├── ExecutionPlanBuilder.java
    ├── TaskDependencyResolver.java
    ├── WorkflowEngine.java
    ├── WorkflowResult.java
    ├── WorkflowTaskDispatcher.java
    ├── WorkflowTaskStatus.java
    └── TaskContextBuilder.java
```

## Package rules

- Keep the package names plural.
- Do not reintroduce `StructuredAiModelGateway` unless a future architectural decision explicitly requires it.
- Runtime classes are not automatically JPA entities.
- REST contracts belong in `dtos`.
- Persistence belongs in `entities` and `repositories`.
- Application lifecycle/orchestration belongs in `services`.
- Workflow runtime mechanics belong in `workflows`.
