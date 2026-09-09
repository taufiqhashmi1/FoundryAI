package com.taufiqhashmi.foundryai.workflows;

import com.taufiqhashmi.foundryai.agents.AgentTask;
import com.taufiqhashmi.foundryai.agents.AgentType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class ExecutionPlanBuilder {

        public ExecutionPlan build(
                        List<PlannedTask> plannedTasks) {
                if (plannedTasks == null ||
                                plannedTasks.isEmpty()) {

                        throw new IllegalArgumentException(
                                        "Planned tasks cannot be null or empty");
                }

                Map<String, UUID> taskIds = generateTaskIds(plannedTasks);

                List<AgentTask> tasks = plannedTasks.stream()
                                .map(task -> convertTask(
                                                task,
                                                taskIds))
                                .toList();

                return ExecutionPlan.builder()
                                .tasks(tasks)
                                .build();
        }

        private Map<String, UUID> generateTaskIds(
                        List<PlannedTask> plannedTasks) {
                Map<String, UUID> taskIds = new HashMap<>();

                for (PlannedTask task : plannedTasks) {

                        validatePlannedTask(task);

                        if (taskIds.containsKey(
                                        task.getTaskKey())) {

                                throw new IllegalArgumentException(
                                                "Duplicate task key: "
                                                                + task.getTaskKey());
                        }

                        taskIds.put(
                                        task.getTaskKey(),
                                        UUID.randomUUID());
                }

                return taskIds;
        }

        private void validatePlannedTask(
                        PlannedTask task) {
                if (task == null) {
                        throw new IllegalArgumentException(
                                        "Planned task cannot be null");
                }

                if (task.getTaskKey() == null ||
                                task.getTaskKey().isBlank()) {

                        throw new IllegalArgumentException(
                                        "Every planned task must have a task key");
                }

                if (task.getAgentType() == null) {
                        throw new IllegalArgumentException(
                                        "Planned task must have an agent type");
                }

                if (task.getAgentType() == AgentType.CEO) {
                        throw new IllegalArgumentException(
                                        "CEO cannot be used as a specialist workflow task");
                }

                if (task.getObjective() == null ||
                                task.getObjective().isBlank()) {

                        throw new IllegalArgumentException(
                                        "Planned task must have an objective");
                }
        }

        private AgentTask convertTask(
                        PlannedTask plannedTask,
                        Map<String, UUID> taskIds) {
                return AgentTask.builder()
                                .taskId(
                                                taskIds.get(
                                                                plannedTask.getTaskKey()))
                                .agentType(
                                                plannedTask.getAgentType())
                                .objective(
                                                plannedTask.getObjective())
                                .dependencies(
                                                resolveDependencies(
                                                                plannedTask,
                                                                taskIds))
                                .build();
        }

        private List<UUID> resolveDependencies(
                        PlannedTask plannedTask,
                        Map<String, UUID> taskIds) {
                if (plannedTask.getDependencies() == null ||
                                plannedTask.getDependencies().isEmpty()) {

                        return List.of();
                }

                return plannedTask.getDependencies()
                                .stream()
                                .map(dependencyKey -> {

                                        UUID dependencyId = taskIds.get(dependencyKey);

                                        if (dependencyId == null) {
                                                throw new IllegalArgumentException(
                                                                "Task "
                                                                                + plannedTask.getTaskKey()
                                                                                + " depends on unknown task key: "
                                                                                + dependencyKey);
                                        }

                                        return dependencyId;
                                })
                                .toList();
        }
}