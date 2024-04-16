package com.jcs.interview.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TeamDto(
    @NotBlank(message = "name must not be null or blank") String name,
    String foundingDate
) {
}
