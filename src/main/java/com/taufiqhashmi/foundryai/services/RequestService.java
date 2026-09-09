package com.taufiqhashmi.foundryai.services;

import com.taufiqhashmi.foundryai.dtos.CreateRequestDTO;
import com.taufiqhashmi.foundryai.dtos.RequestResponseDTO;
import com.taufiqhashmi.foundryai.entities.Request;
import com.taufiqhashmi.foundryai.entities.RequestStatus;
import com.taufiqhashmi.foundryai.exceptions.ResourceNotFoundException;
import com.taufiqhashmi.foundryai.repositories.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;

    @Transactional
    public RequestResponseDTO createRequest(
            CreateRequestDTO requestDTO
    ) {
        if (requestDTO == null) {
            throw new IllegalArgumentException(
                    "Request cannot be null"
            );
        }

        if (requestDTO.getObjective() == null ||
                requestDTO.getObjective().isBlank()) {
            throw new IllegalArgumentException(
                    "Objective cannot be null or blank"
            );
        }

        Instant now = Instant.now();

        Request request = new Request();
        request.setObjective(requestDTO.getObjective().trim());
        request.setStatus(RequestStatus.CREATED);
        request.setCreatedAt(now);
        request.setUpdatedAt(now);

        request = requestRepository.save(request);

        return toResponseDTO(request);
    }

    @Transactional(readOnly = true)
    public RequestResponseDTO getRequest(
            UUID requestId
    ) {
        if (requestId == null) {
            throw new IllegalArgumentException(
                    "Request ID cannot be null"
            );
        }

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Request not found: " + requestId
                        )
                );

        return toResponseDTO(request);
    }

    private RequestResponseDTO toResponseDTO(
            Request request
    ) {
        return RequestResponseDTO.builder()
                .id(request.getId())
                .userId(request.getUserId())
                .objective(request.getObjective())
                .status(request.getStatus())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }
}