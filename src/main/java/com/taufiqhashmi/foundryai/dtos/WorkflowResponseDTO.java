package com.taufiqhashmi.foundryai.dtos;

import com.taufiqhashmi.foundryai.entities.WorkflowStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowResponseDTO {

    private UUID id;
    private UUID requestId;
    private WorkflowStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    private CEORecommendationResponseDTO recommendation;

    private List<WorkflowTaskResponseDTO> tasks;
}