package com.taufiqhashmi.foundryai.dtos;

import com.taufiqhashmi.foundryai.entities.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowTaskResponseDTO {

    private UUID id;

    private String agentType;

    private String objective;

    private TaskStatus status;

    private Integer executionOrder;

    private Instant startedAt;

    private Instant completedAt;

    private String result;

    private String errorMessage;
}