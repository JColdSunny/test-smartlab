package com.jcs.interview.service;

import com.jcs.interview.dto.GameInfoDto;
import com.jcs.interview.dto.LeagueDto;
import com.jcs.interview.dto.RoundInfoDto;
import com.jcs.interview.dto.ScheduleInfoDto;
import com.jcs.interview.dto.TeamDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameScheduleService {
    private static final Logger LOG = LoggerFactory.getLogger(GameScheduleService.class);

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final int WEEKS_BETWEEN_ROUNDS = 3;
    private static final int WEEKS_BETWEEN_GAMES = 1;
    private static final int FIRST_LIST_INDEX = 0;
    private static final int ONE_TEAM = 1;
    private static final int DIVISION_FACTOR = 2;

    private final String startDate;
    private final String startTime;

    public GameScheduleService(@Value("${application.game.start.date}") String startDate,
                               @Value("${application.game.start.time}") String startTime) {
        this.startDate = startDate;
        this.startTime = startTime;
    }

    public ScheduleInfoDto generateGameSchedule(LeagueDto league) {
        List<TeamDto> teams = league.teams();
        if (isTeamCountOdd(teams.size())) {
            throw new IllegalStateException("The number of teams must be even");
        }

        int gamesInEachRound = teams.size() - 1;
        LocalDateTime firstRoundStart = LocalDateTime.parse(startDate + "T" + startTime)
                .atZone(ZoneOffset.UTC)
                .withFixedOffsetZone()
                .toLocalDateTime();

        List<LocalDateTime> firstRoundDates = getSaturdayDates(firstRoundStart, gamesInEachRound);

        RoundInfoDto firstRoundInfo = generateRound(teams, firstRoundDates, Boolean.FALSE);

        LocalDateTime firstRoundLastDate = firstRoundDates.getLast();
        LocalDateTime secondRoundStart = firstRoundLastDate.plusWeeks(WEEKS_BETWEEN_ROUNDS);

        List<LocalDateTime> secondRoundDates = getSaturdayDates(secondRoundStart, gamesInEachRound);

        RoundInfoDto secondRoundInfo = generateRound(teams, secondRoundDates, Boolean.TRUE);

        ScheduleInfoDto scheduleInfo = new ScheduleInfoDto(List.of(firstRoundInfo, secondRoundInfo));
        logSchedule(scheduleInfo);
        return scheduleInfo;
    }

    private static boolean isTeamCountOdd(int teamSize) {
        return teamSize % 2 != 0;
    }

    private static List<LocalDateTime> getSaturdayDates(LocalDateTime startDate, int weeks) {
        List<LocalDateTime> dates = new ArrayList<>();
        for (int i = 0; i < weeks; i++) {
            dates.add(startDate);
            startDate = startDate.plusWeeks(WEEKS_BETWEEN_GAMES);
        }
        return dates;
    }

    private RoundInfoDto generateRound(List<TeamDto> teams,
                                       List<LocalDateTime> dates,
                                       boolean isReverse) {
        List<TeamDto> firstHalfTeams;
        List<TeamDto> secondHalfTeams;

        if (isReverse) {
            firstHalfTeams = new ArrayList<>(teams.subList(teams.size() / DIVISION_FACTOR, teams.size()));
            secondHalfTeams = new ArrayList<>(teams.subList(FIRST_LIST_INDEX, teams.size() / DIVISION_FACTOR));
        } else {
            firstHalfTeams = new ArrayList<>(teams.subList(FIRST_LIST_INDEX, teams.size() / DIVISION_FACTOR));
            secondHalfTeams = new ArrayList<>(teams.subList(teams.size() / DIVISION_FACTOR, teams.size()));
        }

        List<GameInfoDto> games = new ArrayList<>();
        for (LocalDateTime date : dates) {
            for (int i = 0; i < teams.size() / DIVISION_FACTOR; i++) {
                games.add(new GameInfoDto(date, firstHalfTeams.get(i).name(), secondHalfTeams.get(i).name()));
            }

            if (secondHalfTeams.size() != ONE_TEAM) {
                secondHalfTeams.add(FIRST_LIST_INDEX, firstHalfTeams.remove(1));
                firstHalfTeams.add(secondHalfTeams.remove(secondHalfTeams.size() - 1));
            }
        }

        return new RoundInfoDto(dates.getFirst(), games);
    }

    private static void logSchedule(ScheduleInfoDto scheduleInfo) {
        String games = scheduleInfo.rounds().stream()
                .flatMap(round -> round.games().stream())
                .map(game ->
                        "%s; %s; %s".formatted(DATE_TIME_FORMATTER.format(game.date()), game.firstTeam(), game.secondTeam())
                ).collect(Collectors.joining(System.lineSeparator()));
        LOG.info(games);
    }

}
