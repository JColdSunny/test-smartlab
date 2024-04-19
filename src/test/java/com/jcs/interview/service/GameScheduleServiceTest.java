package com.jcs.interview.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.jcs.interview.dto.GameInfoDto;
import com.jcs.interview.dto.LeagueDto;
import com.jcs.interview.dto.RoundInfoDto;
import com.jcs.interview.dto.ScheduleInfoDto;
import com.jcs.interview.dto.TeamDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = {GameScheduleService.class})
public class GameScheduleServiceTest {

    @Autowired
    private GameScheduleService gameScheduleService;

    @Value("classpath:smartlab_soccer_teams.json")
    private Resource soccerTeamsResource;

    private static ObjectMapper objectMapper;

    @BeforeAll
    static void init() {
        objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    }

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
                .isInstanceOf(IllegalStateException.class)
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
                        if (games.get(i).date().equals(games.get(i + 1).date())) {
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
        LocalDateTime firstRoundLastDate = scheduleInfoDto.rounds().getFirst().games().getLast().date();
        LocalDateTime secondRoundFirstDate = scheduleInfoDto.rounds().getLast().roundStartDate();

        assertThat(scheduleInfoDto).isNotNull();
        assertThat(scheduleInfoDto.rounds()).hasSize(2);
        assertThat(ChronoUnit.WEEKS.between(firstRoundLastDate, secondRoundFirstDate)).isEqualTo(3);
    }

    @Test
    public void testGenerateNoRepeatedGames() throws IOException {
        LeagueDto league = objectMapper.readValue(soccerTeamsResource.getFile(), LeagueDto.class);

        ScheduleInfoDto scheduleInfoDto = gameScheduleService.generateGameSchedule(league);
        List<GameInfoDto> gameInLeague = scheduleInfoDto.rounds().stream()
                .flatMap(round -> round.games().stream())
                .toList();
        Set<GameInfoDto> uniqueGames = new HashSet<>(gameInLeague);

        assertThat(gameInLeague.size()).isEqualTo(uniqueGames.size());
    }

}