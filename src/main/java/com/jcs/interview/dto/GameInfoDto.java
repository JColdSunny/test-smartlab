package com.jcs.interview.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.OffsetDateTime;

public record GameInfoDto(
        @JsonFormat(pattern = "dd.MM.yyyy HH:mm")
        OffsetDateTime date,
        String firstTeam,
        String secondTeam
) {
}
