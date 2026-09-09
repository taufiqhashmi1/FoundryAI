package com.taufiqhashmi.foundryai.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRequestDTO {

    @NotBlank(message = "Objective is required")
    @Size(
            min = 3,
            max = 5000,
            message = "Objective must be between 3 and 5000 characters"
    )
    private String objective;
}