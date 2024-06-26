package com.jcs.interview.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcs.interview.dto.GameInfoDto;
import com.jcs.interview.dto.LeagueDto;
import com.jcs.interview.dto.RoundInfoDto;
import com.jcs.interview.dto.ScheduleInfoDto;
import com.jcs.interview.dto.TeamDto;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = {GameScheduleService.class})
public class GameScheduleServiceTest {

    @Autowired
    private GameScheduleService gameScheduleService;

    @Value("classpath:smartlab_soccer_teams.json")
    private Resource soccerTeamsResource;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testGenerateWhenEvenNumberOfTeams() {
        List<TeamDto> evenTeams = List.of(
                new TeamDto("Team 1", ""),
                new TeamDto("Team 2", "")
        );
        LeagueDto league = new LeagueDto("league", "country", evenTeams);

        assertThatNoException().isThrownBy(() -> gameScheduleService.generateGameSchedule(league));
    }

    @Test
    public void testGenerateWhenOddNumberOfTeams() {
        List<TeamDto> oddTeams = List.of(
                new TeamDto("Team 1", ""),
                new TeamDto("Team 2", ""),
                new TeamDto("Team 3", "")
        );
        LeagueDto league = new LeagueDto("league", "country", oddTeams);

        assertThatThrownBy(() -> gameScheduleService.generateGameSchedule(league))
                .isInstanceOf(ValidationException.class)
                .hasMessage("The number of teams must be even");
    }

    @Test
    public void testGenerateRoundScheduleCorrectNumberOfGamesInRound() throws IOException {
        LeagueDto league = objectMapper.readValue(soccerTeamsResource.getFile(), LeagueDto.class);
        int oneTeamGamesInRound = (league.teams().size() - 1);
        int countOfGamesInWeek = league.teams().size() / 2;
        int expectedGamesInRound = countOfGamesInWeek * oneTeamGamesInRound;

        ScheduleInfoDto scheduleInfoDto = gameScheduleService.generateGameSchedule(league);

        assertThat(scheduleInfoDto).isNotNull();
        assertThat(scheduleInfoDto.rounds()).hasSize(2);
        assertThat(scheduleInfoDto.rounds().getFirst().games()).hasSize(expectedGamesInRound);
    }

    @Test
    public void testGenerateOneWeekBetweenEachTeamGame() throws IOException {
        LeagueDto league = objectMapper.readValue(soccerTeamsResource.getFile(), LeagueDto.class);

        ScheduleInfoDto scheduleInfoDto = gameScheduleService.generateGameSchedule(league);

        assertThat(scheduleInfoDto).isNotNull();
        assertThat(scheduleInfoDto.rounds()).hasSize(2);
        scheduleInfoDto.rounds().stream()
                .map(RoundInfoDto::games)
                .forEach(games -> {
                    for (int i = 0; i < games.size() - 1; i++) {
                        if (games.get(i).date().equals(games.get(i + 1).date())) { // skip the same date games
                            continue;
                        }

                        assertThat(ChronoUnit.WEEKS.between(games.get(i).date(), games.get(i + 1).date()))
                                .isEqualTo(1);
                    }
                });
    }

    @Test
    public void testGenerateThreeWeeksBetweenRounds() throws IOException {
        LeagueDto league = objectMapper.readValue(soccerTeamsResource.getFile(), LeagueDto.class);

        ScheduleInfoDto scheduleInfoDto = gameScheduleService.generateGameSchedule(league);
        OffsetDateTime firstRoundLastDate = scheduleInfoDto.rounds().getFirst().games().getLast().date();
        OffsetDateTime secondRoundFirstDate = scheduleInfoDto.rounds().getLast().roundStartDate();

        assertThat(scheduleInfoDto).isNotNull();
        assertThat(scheduleInfoDto.rounds()).hasSize(2);
        assertThat(ChronoUnit.WEEKS.between(firstRoundLastDate, secondRoundFirstDate)).isEqualTo(3);
    }

    @Test
    public void testEachTeamPlayedOnlyOnceAgainstEachOther() throws IOException {
        LeagueDto league = objectMapper.readValue(soccerTeamsResource.getFile(), LeagueDto.class);

        ScheduleInfoDto scheduleInfoDto = gameScheduleService.generateGameSchedule(league);

        for (RoundInfoDto round : scheduleInfoDto.rounds()) {
            Map<String, List<String>> teamOpponentsMap = new HashMap<>();
            for (GameInfoDto game : round.games()) {
                // add each team matches in a round
                teamOpponentsMap.computeIfAbsent(game.firstTeam(), k -> new ArrayList<>()).add(game.secondTeam());
                teamOpponentsMap.computeIfAbsent(game.secondTeam(), k -> new ArrayList<>()).add(game.firstTeam());
            }

            teamOpponentsMap.forEach((team, opponents) -> {
                assertThat(opponents).hasSize(league.teams().size() - 1);  // each team played against all others
                assertThat(opponents).hasSameSizeAs(new HashSet<>(opponents));  // no opponent is repeated
            });
        }
    }

}