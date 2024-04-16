package com.jcs.interview.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public record RoundInfoDto(
        @JsonFormat(pattern = "dd.MM.yyyy HH:mm")
        LocalDateTime roundStartDate,
        List<GameInfoDto> games
) {
}
