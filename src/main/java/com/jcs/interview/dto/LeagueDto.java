package com.jcs.interview.dto;

import java.util.List;

public record LeagueDto(
    String league,
    String country,
    List<TeamDto> teams
) {
}
