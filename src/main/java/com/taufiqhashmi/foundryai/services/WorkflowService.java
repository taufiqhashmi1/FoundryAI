package com.taufiqhashmi.foundryai.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.taufiqhashmi.foundryai.agents.AgentResult;
import com.taufiqhashmi.foundryai.agents.AgentTask;
import com.taufiqhashmi.foundryai.agents.CEOPlanner;
import com.taufiqhashmi.foundryai.agents.CEOSynthesizer;

import com.taufiqhashmi.foundryai.dtos.CEORecommendationResponseDTO;
import com.taufiqhashmi.foundryai.dtos.WorkflowResponseDTO;
import com.taufiqhashmi.foundryai.dtos.WorkflowTaskResponseDTO;

import com.taufiqhashmi.foundryai.entities.AgentExecution;
import com.taufiqhashmi.foundryai.entities.AgentExecutionStatus;
import com.taufiqhashmi.foundryai.entities.Request;
import com.taufiqhashmi.foundryai.entities.RequestStatus;
import com.taufiqhashmi.foundryai.entities.TaskStatus;
import com.taufiqhashmi.foundryai.entities.Workflow;
import com.taufiqhashmi.foundryai.entities.WorkflowStatus;
import com.taufiqhashmi.foundryai.entities.WorkflowTask;

import com.taufiqhashmi.foundryai.exceptions.ResourceNotFoundException;

import com.taufiqhashmi.foundryai.repositories.AgentExecutionRepository;
import com.taufiqhashmi.foundryai.repositories.RequestRepository;
import com.taufiqhashmi.foundryai.repositories.WorkflowRepository;
import com.taufiqhashmi.foundryai.repositories.WorkflowTaskRepository;

import com.taufiqhashmi.foundryai.workflows.ExecutionPlan;
import com.taufiqhashmi.foundryai.workflows.WorkflowEngine;
import com.taufiqhashmi.foundryai.workflows.WorkflowResult;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final RequestRepository requestRepository;
    private final WorkflowRepository workflowRepository;
    private final WorkflowTaskRepository workflowTaskRepository;
    private final AgentExecutionRepository agentExecutionRepository;

    private final CEOPlanner ceoPlanner;
    private final WorkflowEngine workflowEngine;
    private final CEOSynthesizer ceoSynthesizer;

    private final ObjectMapper objectMapper;


    public WorkflowResponseDTO executeWorkflow(UUID requestId) {

        Request request = getRequestEntity(requestId);

        updateRequestStatus(
                request,
                RequestStatus.PROCESSING
        );

        Workflow workflow = createWorkflow(request);

        try {

            /*
             * 1. CEO creates the execution plan.
             */
            ExecutionPlan executionPlan =
                    ceoPlanner.createPlan(
                            request.getObjective()
                    );


            /*
             * 2. Persist workflow tasks.
             */
            List<WorkflowTask> workflowTasks =
                    createWorkflowTasks(
                            workflow,
                            executionPlan
                    );


            /*
             * 3. Execute specialist workflow.
             */
            WorkflowResult workflowResult =
                    workflowEngine.execute(
                            executionPlan
                    );


            /*
             * 4. Persist specialist results.
             */
            persistWorkflowResults(
                    workflowTasks,
                    executionPlan,
                    workflowResult
            );


            /*
             * 5. CEO synthesizes the specialist results.
             */
            if (workflowResult.isSuccessful()) {

                CEORecommendationResponseDTO recommendation =
                        ceoSynthesizer.synthesize(
                                request.getObjective(),
                                workflowResult.getResults()
                        );


                /*
                 * 6. Persist the final CEO recommendation.
                 */
                persistRecommendation(
                        workflow,
                        recommendation
                );


                workflow.setStatus(
                        WorkflowStatus.COMPLETED
                );

                updateRequestStatus(
                        request,
                        RequestStatus.COMPLETED
                );

            } else {

                workflow.setStatus(
                        WorkflowStatus.FAILED
                );

                updateRequestStatus(
                        request,
                        RequestStatus.FAILED
                );
            }

        } catch (Exception exception) {

            workflow.setStatus(
                    WorkflowStatus.FAILED
            );

            updateRequestStatus(
                    request,
                    RequestStatus.FAILED
            );

            workflowRepository.save(workflow);

            throw exception;
        }

        workflow.setUpdatedAt(Instant.now());

        workflowRepository.save(workflow);

        return toResponseDTO(workflow);
    }


    @Transactional(readOnly = true)
    public WorkflowResponseDTO getWorkflow(UUID workflowId) {

        if (workflowId == null) {
            throw new IllegalArgumentException(
                    "Workflow ID cannot be null"
            );
        }

        Workflow workflow =
                workflowRepository.findById(workflowId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Workflow not found: "
                                                + workflowId
                                )
                        );

        return toResponseDTO(workflow);
    }


    private Request getRequestEntity(UUID requestId) {

        if (requestId == null) {
            throw new IllegalArgumentException(
                    "Request ID cannot be null"
            );
        }

        return requestRepository.findById(requestId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Request not found: "
                                        + requestId
                        )
                );
    }


    private Workflow createWorkflow(Request request) {

        Instant now = Instant.now();

        Workflow workflow = new Workflow();

        workflow.setRequest(request);
        workflow.setStatus(WorkflowStatus.CREATED);
        workflow.setCreatedAt(now);
        workflow.setUpdatedAt(now);

        return workflowRepository.save(workflow);
    }


    private List<WorkflowTask> createWorkflowTasks(
            Workflow workflow,
            ExecutionPlan executionPlan
    ) {

        List<WorkflowTask> tasks =
                new ArrayList<>();

        List<AgentTask> agentTasks =
                executionPlan.getTasks();

        for (int i = 0;
             i < agentTasks.size();
             i++) {

            AgentTask agentTask =
                    agentTasks.get(i);

            WorkflowTask workflowTask =
                    new WorkflowTask();

            workflowTask.setWorkflow(workflow);

            workflowTask.setAgentType(
                    agentTask
                            .getAgentType()
                            .name()
            );

            workflowTask.setObjective(
                    agentTask.getObjective()
            );

            workflowTask.setStatus(
                    TaskStatus.PENDING
            );

            workflowTask.setExecutionOrder(i);

            tasks.add(workflowTask);
        }

        workflow.getTasks().addAll(tasks);

        return workflowTaskRepository.saveAll(tasks);
    }


    private void persistWorkflowResults(
            List<WorkflowTask> workflowTasks,
            ExecutionPlan executionPlan,
            WorkflowResult workflowResult
    ) {

        Map<UUID, WorkflowTask>
                workflowTasksByRuntimeId =
                mapWorkflowTasks(
                        workflowTasks,
                        executionPlan
                );

        for (Map.Entry<UUID, AgentResult> entry :
                workflowResult
                        .getResults()
                        .entrySet()) {

            UUID runtimeTaskId =
                    entry.getKey();

            AgentResult agentResult =
                    entry.getValue();

            WorkflowTask workflowTask =
                    workflowTasksByRuntimeId
                            .get(runtimeTaskId);

            if (workflowTask == null) {
                continue;
            }

            Instant now = Instant.now();

            workflowTask.setStatus(
                    agentResult.isSuccessful()
                            ? TaskStatus.COMPLETED
                            : TaskStatus.FAILED
            );

            workflowTask.setResult(
                    agentResult.getOutput()
            );

            workflowTask.setErrorMessage(
                    agentResult.getError()
            );

            workflowTask.setStartedAt(now);
            workflowTask.setCompletedAt(now);

            workflowTaskRepository.save(
                    workflowTask
            );


            AgentExecution execution =
                    new AgentExecution();

            execution.setWorkflowTask(
                    workflowTask
            );

            execution.setAgentType(
                    agentResult
                            .getAgentType()
                            .name()
            );

            execution.setAttempt(1);

            execution.setStatus(
                    agentResult.isSuccessful()
                            ? AgentExecutionStatus.COMPLETED
                            : AgentExecutionStatus.FAILED
            );

            execution.setOutput(
                    agentResult.getOutput()
            );

            execution.setErrorMessage(
                    agentResult.getError()
            );

            execution.setStartedAt(now);
            execution.setCompletedAt(now);

            agentExecutionRepository.save(
                    execution
            );
        }
    }


    private void persistRecommendation(
            Workflow workflow,
            CEORecommendationResponseDTO recommendation
    ) {

        if (recommendation == null) {
            throw new IllegalStateException(
                    "CEO recommendation cannot be null"
            );
        }

        try {

            String recommendationJson =
                    objectMapper.writeValueAsString(
                            recommendation
                    );

            workflow.setRecommendation(
                    recommendationJson
            );

            workflow.setUpdatedAt(
                    Instant.now()
            );

            workflowRepository.save(workflow);

        } catch (JsonProcessingException exception) {

            throw new IllegalStateException(
                    "Failed to serialize CEO recommendation",
                    exception
            );
        }
    }


    private Map<UUID, WorkflowTask> mapWorkflowTasks(
            List<WorkflowTask> workflowTasks,
            ExecutionPlan executionPlan
    ) {

        List<AgentTask> agentTasks =
                executionPlan.getTasks();

        return java.util.stream.IntStream
                .range(0, agentTasks.size())
                .boxed()
                .collect(
                        Collectors.toMap(
                                i -> agentTasks
                                        .get(i)
                                        .getTaskId(),
                                workflowTasks::get
                        )
                );
    }


    private void updateRequestStatus(
            Request request,
            RequestStatus status
    ) {

        request.setStatus(status);
        request.setUpdatedAt(
                Instant.now()
        );

        requestRepository.save(request);
    }


    private WorkflowResponseDTO toResponseDTO(
            Workflow workflow
    ) {

        List<WorkflowTaskResponseDTO>
                taskDTOs =
                workflow.getTasks()
                        .stream()
                        .map(this::toTaskResponseDTO)
                        .toList();


        CEORecommendationResponseDTO
                recommendation =
                deserializeRecommendation(
                        workflow.getRecommendation()
                );


        return WorkflowResponseDTO.builder()
                .id(workflow.getId())
                .requestId(
                        workflow
                                .getRequest()
                                .getId()
                )
                .status(workflow.getStatus())
                .createdAt(
                        workflow.getCreatedAt()
                )
                .updatedAt(
                        workflow.getUpdatedAt()
                )
                .recommendation(
                        recommendation
                )
                .tasks(taskDTOs)
                .build();
    }


    private CEORecommendationResponseDTO
    deserializeRecommendation(
            String recommendationJson
    ) {

        if (recommendationJson == null ||
                recommendationJson.isBlank()) {

            return null;
        }

        try {

            return objectMapper.readValue(
                    recommendationJson,
                    CEORecommendationResponseDTO.class
            );

        } catch (JsonProcessingException exception) {

            throw new IllegalStateException(
                    "Failed to deserialize CEO recommendation",
                    exception
            );
        }
    }


    private WorkflowTaskResponseDTO
    toTaskResponseDTO(
            WorkflowTask task
    ) {

        return WorkflowTaskResponseDTO.builder()
                .id(task.getId())
                .agentType(task.getAgentType())
                .objective(task.getObjective())
                .status(task.getStatus())
                .executionOrder(
                        task.getExecutionOrder()
                )
                .startedAt(
                        task.getStartedAt()
                )
                .completedAt(
                        task.getCompletedAt()
                )
                .result(
                        task.getResult()
                )
                .errorMessage(
                        task.getErrorMessage()
                )
                .build();
    }
}