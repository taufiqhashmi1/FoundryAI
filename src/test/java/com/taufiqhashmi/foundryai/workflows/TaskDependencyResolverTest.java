package com.taufiqhashmi.foundryai.workflows;

import com.taufiqhashmi.foundryai.agents.AgentTask;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskDependencyResolverTest {

    private final TaskDependencyResolver resolver =
            new TaskDependencyResolver();

    @Test
    void shouldReturnTasksWithNoDependencies() {

        UUID taskId = UUID.randomUUID();

        AgentTask task = AgentTask.builder()
                .taskId(taskId)
                .objective("Run independent task")
                .build();

        List<AgentTask> runnableTasks =
                resolver.findRunnableTasks(
                        List.of(task),
                        Set.of()
                );

        assertEquals(
                1,
                runnableTasks.size()
        );

        assertEquals(
                taskId,
                runnableTasks.get(0).getTaskId()
        );
    }

    @Test
    void shouldNotReturnTaskWhenDependencyIsIncomplete() {

        UUID dependencyId = UUID.randomUUID();

        AgentTask task = AgentTask.builder()
                .taskId(UUID.randomUUID())
                .objective("Dependent task")
                .dependencies(List.of(dependencyId))
                .build();

        List<AgentTask> runnableTasks =
                resolver.findRunnableTasks(
                        List.of(task),
                        Set.of()
                );

        assertEquals(
                0,
                runnableTasks.size()
        );
    }

    @Test
    void shouldReturnTaskWhenAllDependenciesAreCompleted() {

        UUID dependencyOne = UUID.randomUUID();
        UUID dependencyTwo = UUID.randomUUID();

        AgentTask task = AgentTask.builder()
                .taskId(UUID.randomUUID())
                .objective("Dependent task")
                .dependencies(
                        List.of(
                                dependencyOne,
                                dependencyTwo
                        )
                )
                .build();

        List<AgentTask> runnableTasks =
                resolver.findRunnableTasks(
                        List.of(task),
                        Set.of(
                                dependencyOne,
                                dependencyTwo
                        )
                );

        assertEquals(
                1,
                runnableTasks.size()
        );

        assertEquals(
                task.getTaskId(),
                runnableTasks.get(0).getTaskId()
        );
    }

    @Test
    void shouldNotReturnTaskWhenOnlySomeDependenciesAreCompleted() {

        UUID dependencyOne = UUID.randomUUID();
        UUID dependencyTwo = UUID.randomUUID();

        AgentTask task = AgentTask.builder()
                .taskId(UUID.randomUUID())
                .objective("Dependent task")
                .dependencies(
                        List.of(
                                dependencyOne,
                                dependencyTwo
                        )
                )
                .build();

        List<AgentTask> runnableTasks =
                resolver.findRunnableTasks(
                        List.of(task),
                        Set.of(dependencyOne)
                );

        assertEquals(
                0,
                runnableTasks.size()
        );
    }
}