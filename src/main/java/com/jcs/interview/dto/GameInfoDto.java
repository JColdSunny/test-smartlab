package com.jcs.interview.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record GameInfoDto(
        @JsonFormat(pattern = "dd.MM.yyyy HH:mm")
        LocalDateTime date,
        String firstTeam,
        String secondTeam
) {
}
