package com.jcs.interview.service;

import com.jcs.interview.dto.LeagueDto;
import com.jcs.interview.dto.TeamDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class GameScheduleService {
    private static final int WEEKS_BETWEEN_ROUNDS = 3;
    private static final int WEEKS_BETWEEN_GAMES = 1;
    private static final String NEW_LINE = "\n";
    private static final String SEMI_COLON = ";";
    private static final String SPACE = " ";
    private static final int FIRST_LIST_INDEX = 0;
    private static final int TEAM_NUMBER = 1;
    private static final int DIVISION_FACTOR = 2;

    private final String startDate;
    private final String startTime;

    public GameScheduleService(@Value("${application.game.start.date}")String startDate,
                               @Value("${application.game.start.time}")String startTime) {
        this.startDate = startDate;
        this.startTime = startTime;
    }

    public String generate(LeagueDto league) {
        List<TeamDto> teams = league.teams();

        if (teams.isEmpty()) {
            throw new IllegalArgumentException("No teams found");
        }
        if (teams.size() % 2 != 0) {
            throw new IllegalStateException("The number of teams must be even");
        }

        int gamesInEachRound = teams.size() - 1;
        LocalDate start = LocalDate.parse(startDate);
        List<LocalDate> firstRoundDates = getSaturdayDates(start, gamesInEachRound);

        String firstRoundGames = generateRound(teams, firstRoundDates, Boolean.FALSE);

        LocalDate firstRoundLastDate = firstRoundDates.get(firstRoundDates.size() - 1);
        LocalDate secondRoundStart = firstRoundLastDate.plusWeeks(WEEKS_BETWEEN_ROUNDS);
        List<LocalDate> secondRoundDates = getSaturdayDates(secondRoundStart, gamesInEachRound);

        String secondRoundGames = generateRound(teams, secondRoundDates, Boolean.TRUE);

        return firstRoundGames + NEW_LINE + secondRoundGames;
    }

    private static List<LocalDate> getSaturdayDates(LocalDate startDate, int weeks) {
        List<LocalDate> dates = new ArrayList<>();
        for (int i = 0; i < weeks; i++) {
            dates.add(startDate);
            startDate = startDate.plusWeeks(WEEKS_BETWEEN_GAMES);
        }
        return dates;
    }

    private String generateRound(List<TeamDto> teams,
                                 List<LocalDate> dates,
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

        StringBuilder builder = new StringBuilder();
        for (LocalDate date : dates) {
            for (int i = 0; i < teams.size() / DIVISION_FACTOR; i++) {
                builder.append(date)
                        .append(SPACE).append(startTime).append(SEMI_COLON).append(SPACE)
                        .append(firstHalfTeams.get(i).name()).append(SEMI_COLON).append(SPACE)
                        .append(secondHalfTeams.get(i).name()).append(SEMI_COLON)
                        .append(NEW_LINE);
            }

            if (secondHalfTeams.size() != TEAM_NUMBER) {
                secondHalfTeams.add(FIRST_LIST_INDEX, firstHalfTeams.remove(1));
                firstHalfTeams.add(secondHalfTeams.remove(secondHalfTeams.size() - 1));
            }
        }

        return builder.toString();
    }

}
