package com.taufiqhashmi.foundryai.workflows;

import com.taufiqhashmi.foundryai.agents.AgentTask;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class TaskDependencyResolver {

    public List<AgentTask> findRunnableTasks(
            List<AgentTask> tasks,
            Set<UUID> completedTaskIds
    ) {
        if (tasks == null || tasks.isEmpty()) {
            return List.of();
        }

        Set<UUID> completed =
                completedTaskIds == null
                        ? Set.of()
                        : new HashSet<>(completedTaskIds);

        return tasks.stream()
                .filter(task -> dependenciesSatisfied(task, completed))
                .toList();
    }

    private boolean dependenciesSatisfied(
            AgentTask task,
            Set<UUID> completedTaskIds
    ) {
        if (task == null) {
            return false;
        }

        if (task.getDependencies() == null ||
                task.getDependencies().isEmpty()) {

            return true;
        }

        return completedTaskIds.containsAll(
                task.getDependencies()
        );
    }
}