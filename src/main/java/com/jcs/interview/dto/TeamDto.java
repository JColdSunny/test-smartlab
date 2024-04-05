package com.jcs.interview.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TeamDto(
    @NotBlank String name,
    String foundingDate
) {
}
