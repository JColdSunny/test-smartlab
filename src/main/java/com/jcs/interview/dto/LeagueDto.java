package com.jcs.interview.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record LeagueDto(
    @NotBlank(message = "league must not be null or blank") String league,
    @NotBlank(message = "country must not be null or blank") String country,
    @Valid @NotEmpty(message = "teams must not be empty") List <TeamDto> teams
) {
}
