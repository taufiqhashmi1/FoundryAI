package com.taufiqhashmi.foundryai.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "agent_executions")
public class AgentExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workflow_task_id", nullable = false)
    private WorkflowTask workflowTask;

    @Column(nullable = false)
    private String agentType;

    @Column(nullable = false)
    private Integer attempt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgentExecutionStatus status;

    @Column(columnDefinition = "TEXT")
    private String input;

    @Column(columnDefinition = "TEXT")
    private String output;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    private String model;

    @Column(nullable = false, updatable = false)
    private Instant startedAt;

    private Instant completedAt;
}