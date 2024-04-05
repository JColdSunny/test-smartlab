package com.jcs.interview.controller;

import com.jcs.interview.service.SoccerTeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/soccer/teams/schedule")
public class SoccerTeamController {
    private final SoccerTeamService soccerTeamService;

    @PostMapping
    public String createSchedule(@RequestParam("file") MultipartFile file) {
        return soccerTeamService.prepareSchedule(file);
    }

}
