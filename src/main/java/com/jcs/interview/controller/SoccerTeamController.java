package com.jcs.interview.controller;

import com.jcs.interview.dto.LeagueDto;
import com.jcs.interview.dto.ScheduleInfoDto;
import com.jcs.interview.service.GameScheduleService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/soccer/teams/schedule")
public class SoccerTeamController {
    private final GameScheduleService gameScheduleService;

    public SoccerTeamController(GameScheduleService gameScheduleService) {
        this.gameScheduleService = gameScheduleService;
    }

    @PostMapping
    public ScheduleInfoDto createSchedule(@RequestBody @Valid LeagueDto league) {
        return gameScheduleService.generateGameSchedule(league);
    }

}
