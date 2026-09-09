package com.taufiqhashmi.foundryai.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorResponseDTO {

    private Instant timestamp;

    private int status;

    private String error;

    private String message;

    private Map<String, String> errors;
}