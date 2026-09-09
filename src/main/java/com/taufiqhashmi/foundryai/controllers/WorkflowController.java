package com.taufiqhashmi.foundryai.controllers;

import com.taufiqhashmi.foundryai.dtos.WorkflowResponseDTO;
import com.taufiqhashmi.foundryai.services.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/workflows")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowService workflowService;

    @PostMapping("/requests/{requestId}/execute")
    public ResponseEntity<WorkflowResponseDTO> executeWorkflow(
            @PathVariable UUID requestId
    ) {
        return ResponseEntity.ok(
                workflowService.executeWorkflow(requestId)
        );
    }

    @GetMapping("/{workflowId}")
    public ResponseEntity<WorkflowResponseDTO> getWorkflow(
            @PathVariable UUID workflowId
    ) {
        return ResponseEntity.ok(
                workflowService.getWorkflow(workflowId)
        );
    }
}