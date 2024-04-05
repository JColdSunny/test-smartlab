package com.jcs.interview.service;

import com.jcs.interview.dto.LeagueDto;
import com.jcs.interview.dto.TeamDto;
import java.time.LocalDate;
import java.util.ArrayList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GameScheduleServiceTest {

    @Test
    public void testGenerateForEvenNumberOfTeams() {
        var service = new GameScheduleService();
        var teams = new ArrayList<TeamDto>();
        teams.add(new TeamDto("Team 1", ""));
        teams.add(new TeamDto("Team 2", ""));
        var leagueDto = new LeagueDto("", "", teams);

        Assertions.assertDoesNotThrow(() -> service.generate(leagueDto));
    }

    @Test
    public void testGenerateForOddNumberOfTeams() {
        var service = new GameScheduleService();
        var teams = new ArrayList<TeamDto>();
        teams.add(new TeamDto("Team 1", ""));
        teams.add(new TeamDto("Team 2", ""));
        teams.add(new TeamDto("Team 3", ""));
        var leagueDto = new LeagueDto("", "", teams);

        Assertions.assertThrows(IllegalStateException.class, () -> service.generate(leagueDto));
    }

    @Test
    public void testGenerateForNoTeams() {
        var service = new GameScheduleService();
        var teams = new ArrayList<TeamDto>();
        var leagueDto = new LeagueDto("", "", teams);

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.generate(leagueDto));
    }

    @Test
    public void testGenerateRoundSchedule() {
        var service = new GameScheduleService();
        var teams = new ArrayList<TeamDto>();
        teams.add(new TeamDto("Team 1", ""));
        teams.add(new TeamDto("Team 2", ""));
        teams.add(new TeamDto("Team 3", ""));
        teams.add(new TeamDto("Team 4", ""));
        var leagueDto = new LeagueDto("", "", teams);

        String leagueSchedule = service.generate(leagueDto);

        String expectedSchedule = """
            2020-10-17 17:00; Team 1; Team 3;
            2020-10-17 17:00; Team 2; Team 4;
            2020-10-24 17:00; Team 1; Team 2;
            2020-10-24 17:00; Team 4; Team 3;
            2020-10-31 17:00; Team 1; Team 4;
            2020-10-31 17:00; Team 3; Team 2;
            
            2020-11-21 17:00; Team 3; Team 1;
            2020-11-21 17:00; Team 4; Team 2;
            2020-11-28 17:00; Team 3; Team 4;
            2020-11-28 17:00; Team 2; Team 1;
            2020-12-05 17:00; Team 3; Team 2;
            2020-12-05 17:00; Team 1; Team 4;
            """;
        Assertions.assertEquals(expectedSchedule, leagueSchedule);
    }

}