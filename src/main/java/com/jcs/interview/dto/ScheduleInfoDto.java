package com.jcs.interview.dto;

import java.util.List;

public record ScheduleInfoDto(
        List<RoundInfoDto> rounds
) {
}
