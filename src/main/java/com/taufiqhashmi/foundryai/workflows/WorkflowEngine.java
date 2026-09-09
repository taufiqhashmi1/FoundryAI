package com.taufiqhashmi.foundryai.workflows;

import com.taufiqhashmi.foundryai.agents.AgentContext;
import com.taufiqhashmi.foundryai.agents.AgentResult;
import com.taufiqhashmi.foundryai.agents.AgentResultStatus;
import com.taufiqhashmi.foundryai.agents.AgentTask;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
public class WorkflowEngine {

        private final TaskDependencyResolver dependencyResolver;
        private final WorkflowTaskDispatcher taskDispatcher;
        private final TaskContextBuilder taskContextBuilder;

        public WorkflowEngine(
                        TaskDependencyResolver dependencyResolver,
                        WorkflowTaskDispatcher taskDispatcher,
                        TaskContextBuilder taskContextBuilder) {
                this.dependencyResolver = dependencyResolver;
                this.taskDispatcher = taskDispatcher;
                this.taskContextBuilder = taskContextBuilder;
        }

        public WorkflowResult execute(
                        ExecutionPlan executionPlan) {
                validatePlan(executionPlan);

                List<AgentTask> tasks = executionPlan.getTasks();

                Map<UUID, AgentResult> results = new HashMap<>();

                Map<UUID, WorkflowTaskStatus> taskStatuses = new HashMap<>();

                Set<UUID> completedTaskIds = new HashSet<>();

                List<UUID> failedTaskIds = new ArrayList<>();

                List<UUID> blockedTaskIds = new ArrayList<>();

                Set<UUID> processedTaskIds = new HashSet<>();

                for (AgentTask task : tasks) {
                        taskStatuses.put(
                                        task.getTaskId(),
                                        WorkflowTaskStatus.PENDING);
                }

                while (processedTaskIds.size() < tasks.size()) {

                        boolean madeProgress = false;

                        List<AgentTask> remainingTasks = tasks.stream()
                                        .filter(task -> !processedTaskIds.contains(
                                                        task.getTaskId()))
                                        .toList();

                        /*
                         * First block tasks whose dependencies have failed
                         * or been blocked.
                         */
                        for (AgentTask task : remainingTasks) {

                                if (hasFailedDependency(
                                                task,
                                                taskStatuses)) {

                                        taskStatuses.put(
                                                        task.getTaskId(),
                                                        WorkflowTaskStatus.BLOCKED);

                                        blockedTaskIds.add(
                                                        task.getTaskId());

                                        processedTaskIds.add(
                                                        task.getTaskId());

                                        madeProgress = true;
                                }
                        }

                        remainingTasks = tasks.stream()
                                        .filter(task -> !processedTaskIds.contains(
                                                        task.getTaskId()))
                                        .toList();

                        /*
                         * Find tasks whose dependencies have successfully
                         * completed.
                         */
                        List<AgentTask> runnableTasks = dependencyResolver.findRunnableTasks(
                                        remainingTasks,
                                        completedTaskIds);

                        for (AgentTask task : runnableTasks) {

                                taskStatuses.put(
                                                task.getTaskId(),
                                                WorkflowTaskStatus.RUNNING);

                                AgentResult result;

                                try {
                                        AgentContext context = taskContextBuilder.build(
                                                        task,
                                                        results);

                                        task.setContext(context);

                                        result = taskDispatcher.dispatch(task);

                                } catch (RuntimeException exception) {

                                        result = AgentResult.builder()
                                                        .agentType(task.getAgentType())
                                                        .status(AgentResultStatus.FAILURE)
                                                        .error(exception.getMessage())
                                                        .build();
                                }

                                results.put(
                                                task.getTaskId(),
                                                result);

                                processedTaskIds.add(
                                                task.getTaskId());

                                if (result.isSuccessful()) {

                                        taskStatuses.put(
                                                        task.getTaskId(),
                                                        WorkflowTaskStatus.SUCCESS);

                                        completedTaskIds.add(
                                                        task.getTaskId());

                                } else {

                                        taskStatuses.put(
                                                        task.getTaskId(),
                                                        WorkflowTaskStatus.FAILED);

                                        failedTaskIds.add(
                                                        task.getTaskId());
                                }

                                madeProgress = true;
                        }

                        /*
                         * If nothing was executed or blocked, the remaining
                         * graph cannot make progress.
                         */
                        if (!madeProgress) {

                                throw new IllegalStateException(
                                                "Workflow cannot make progress. "
                                                                + "Possible circular dependencies.");
                        }
                }

                return WorkflowResult.builder()
                                .successful(
                                                failedTaskIds.isEmpty()
                                                                && blockedTaskIds.isEmpty())
                                .results(results)
                                .taskStatuses(taskStatuses)
                                .failedTasks(failedTaskIds)
                                .blockedTasks(blockedTaskIds)
                                .build();
        }

        private void validatePlan(
                        ExecutionPlan executionPlan) {

                if (executionPlan == null) {
                        throw new IllegalArgumentException(
                                        "Execution plan cannot be null");
                }

                if (executionPlan.getTasks() == null ||
                                executionPlan.getTasks().isEmpty()) {

                        throw new IllegalArgumentException(
                                        "Execution plan must contain at least one task");
                }

                Set<UUID> taskIds = new HashSet<>();

                for (AgentTask task : executionPlan.getTasks()) {

                        if (task == null) {
                                throw new IllegalArgumentException(
                                                "Execution plan cannot contain null tasks");
                        }

                        if (task.getTaskId() == null) {
                                throw new IllegalArgumentException(
                                                "Every task must have a task ID");
                        }

                        if (!taskIds.add(task.getTaskId())) {
                                throw new IllegalArgumentException(
                                                "Duplicate task ID: "
                                                                + task.getTaskId());
                        }
                }

                validateDependencies(
                                executionPlan.getTasks(),
                                taskIds);
        }

        private void validateDependencies(
                        List<AgentTask> tasks,
                        Set<UUID> taskIds) {

                for (AgentTask task : tasks) {

                        if (task.getDependencies() == null ||
                                        task.getDependencies().isEmpty()) {
                                continue;
                        }

                        for (UUID dependencyId : task.getDependencies()) {

                                if (dependencyId == null) {
                                        throw new IllegalArgumentException(
                                                        "Task "
                                                                        + task.getTaskId()
                                                                        + " contains a null dependency");
                                }

                                if (!taskIds.contains(dependencyId)) {
                                        throw new IllegalArgumentException(
                                                        "Task "
                                                                        + task.getTaskId()
                                                                        + " depends on unknown task: "
                                                                        + dependencyId);
                                }
                        }
                }
        }

        private boolean hasFailedDependency(
                        AgentTask task,
                        Map<UUID, WorkflowTaskStatus> taskStatuses) {
                if (task.getDependencies() == null ||
                                task.getDependencies().isEmpty()) {

                        return false;
                }

                return task.getDependencies()
                                .stream()
                                .anyMatch(dependencyId -> {

                                        WorkflowTaskStatus status = taskStatuses.get(dependencyId);

                                        return status == WorkflowTaskStatus.FAILED
                                                        || status == WorkflowTaskStatus.BLOCKED;
                                });
        }
}