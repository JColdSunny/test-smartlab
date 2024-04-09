package com.jcs.interview.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.jcs.interview.dto.LeagueDto;
import com.jcs.interview.dto.TeamDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringJUnitConfig({GameScheduleService.class})
public class GameScheduleServiceTest {

    @Autowired
    private GameScheduleService gameScheduleService;

    @Value("classpath:smartlab_soccer_teams.json")
    Resource soccerTeamsResource;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(gameScheduleService, "startDate", "2020-10-17");
        ReflectionTestUtils.setField(gameScheduleService, "startTime", "17:00");
    }

    @Test
    public void testGenerateWhenEvenNumberOfTeams() {
        List<TeamDto> evenTeams = List.of(
                new TeamDto("Team 1", ""),
                new TeamDto("Team 2", "")
        );
        LeagueDto league = new LeagueDto("league", "country", evenTeams);

        assertThatNoException().isThrownBy(() -> gameScheduleService.generate(league));
    }

    @Test
    public void testGenerateWhenOddNumberOfTeams() {
        List<TeamDto> oddTeams = List.of(
                new TeamDto("Team 1", ""),
                new TeamDto("Team 2", ""),
                new TeamDto("Team 3", "")
        );
        LeagueDto league = new LeagueDto("league", "country", oddTeams);

        assertThatThrownBy(() -> gameScheduleService.generate(league))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("The number of teams must be even");
    }

    @Test
    public void testGenerateWhenNoTeams() {
        LeagueDto league = new LeagueDto("league", "country", List.of());

        assertThatThrownBy(() -> gameScheduleService.generate(league))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No teams found");
    }

    @Test
    public void testGenerateRoundSchedule() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        LeagueDto league = objectMapper.readValue(soccerTeamsResource.getFile(), LeagueDto.class);

        String leagueSchedule = gameScheduleService.generate(league);

        String expectedSchedule = """
                2020-10-17 17:00; Best marketing; Roaming partners;
                2020-10-17 17:00; Hot operations; Salfish sales;
                2020-10-17 17:00; Curious devops; Whatever comes next;
                2020-10-24 17:00; Best marketing; Hot operations;
                2020-10-24 17:00; Curious devops; Roaming partners;
                2020-10-24 17:00; Whatever comes next; Salfish sales;
                2020-10-31 17:00; Best marketing; Curious devops;
                2020-10-31 17:00; Whatever comes next; Hot operations;
                2020-10-31 17:00; Salfish sales; Roaming partners;
                2020-11-07 17:00; Best marketing; Whatever comes next;
                2020-11-07 17:00; Salfish sales; Curious devops;
                2020-11-07 17:00; Roaming partners; Hot operations;
                2020-11-14 17:00; Best marketing; Salfish sales;
                2020-11-14 17:00; Roaming partners; Whatever comes next;
                2020-11-14 17:00; Hot operations; Curious devops;
                
                2020-12-05 17:00; Roaming partners; Best marketing;
                2020-12-05 17:00; Salfish sales; Hot operations;
                2020-12-05 17:00; Whatever comes next; Curious devops;
                2020-12-12 17:00; Roaming partners; Salfish sales;
                2020-12-12 17:00; Whatever comes next; Best marketing;
                2020-12-12 17:00; Curious devops; Hot operations;
                2020-12-19 17:00; Roaming partners; Whatever comes next;
                2020-12-19 17:00; Curious devops; Salfish sales;
                2020-12-19 17:00; Hot operations; Best marketing;
                2020-12-26 17:00; Roaming partners; Curious devops;
                2020-12-26 17:00; Hot operations; Whatever comes next;
                2020-12-26 17:00; Best marketing; Salfish sales;
                2021-01-02 17:00; Roaming partners; Hot operations;
                2021-01-02 17:00; Best marketing; Curious devops;
                2021-01-02 17:00; Salfish sales; Whatever comes next;
                """;
        assertThat(leagueSchedule).isEqualTo(expectedSchedule);
    }

    @Test
    public void testGenerateThreeWeeksBetweenRounds() {
        List<TeamDto> teams = List.of(
                new TeamDto("Team 1", ""),
                new TeamDto("Team 2", "")
        );
        LeagueDto league = new LeagueDto("league", "country", teams);
        String schedule = gameScheduleService.generate(league);

        String expectedSchedule = """
                2020-10-17 17:00; Team 1; Team 2;
                
                2020-11-07 17:00; Team 2; Team 1;
                """;
        assertThat(schedule).isEqualTo(expectedSchedule);

    }

    @Test
    public void testGenerateOneWeekBetweenRounds() {
        List<TeamDto> teams = List.of(
                new TeamDto("Team 1", ""),
                new TeamDto("Team 2", ""),
                new TeamDto("Team 3", ""),
                new TeamDto("Team 4", "")
        );
        LeagueDto league = new LeagueDto("league", "country", teams);
        String schedule = gameScheduleService.generate(league);

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
        assertThat(schedule).isEqualTo(expectedSchedule);

    }

}