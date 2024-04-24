package com.jcs.interview.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.OffsetDateTime;
import java.util.List;

public record RoundInfoDto(
        @JsonFormat(pattern = "dd.MM.yyyy HH:mm")
        OffsetDateTime roundStartDate,
        List<GameInfoDto> games
) {
}
