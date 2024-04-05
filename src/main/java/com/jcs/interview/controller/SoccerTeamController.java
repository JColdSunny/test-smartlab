package com.jcs.interview.controller;

import com.jcs.interview.dto.LeagueDto;
import com.jcs.interview.service.GameScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/soccer/teams/schedule")
public class SoccerTeamController {
    private final GameScheduleService gameScheduleService;

    @PostMapping
    public String createSchedule(@RequestBody @Valid LeagueDto league) {
        return gameScheduleService.generate(league);
    }

}
