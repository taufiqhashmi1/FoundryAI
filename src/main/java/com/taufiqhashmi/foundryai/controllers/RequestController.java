package com.taufiqhashmi.foundryai.controllers;

import com.taufiqhashmi.foundryai.dtos.CreateRequestDTO;
import com.taufiqhashmi.foundryai.dtos.RequestResponseDTO;
import com.taufiqhashmi.foundryai.services.RequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<RequestResponseDTO> createRequest(
            @Valid @RequestBody CreateRequestDTO requestDTO
    ) {
        RequestResponseDTO response =
                requestService.createRequest(requestDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<RequestResponseDTO> getRequest(
            @PathVariable UUID requestId
    ) {
        RequestResponseDTO response =
                requestService.getRequest(requestId);

        return ResponseEntity.ok(response);
    }
}