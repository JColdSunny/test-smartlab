package com.jcs.interview.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record LeagueDto(
    @NotBlank String league,
    @NotBlank String country,
    @Valid @NotEmpty List <TeamDto> teams
) {
}
