package com.jcs.interview.service;

import com.jcs.interview.dto.LeagueDto;
import com.jcs.interview.dto.TeamDto;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GameScheduleService {

    public String generate(LeagueDto league) {
        List<TeamDto> teams = league.teams();

        if (teams.isEmpty()) {
            throw new IllegalArgumentException("No teams found");
        }
        if (teams.size() % 2 != 0) {
            throw new IllegalStateException("The number of teams must be even");
        }

        int gamesInEachRound = teams.size() - 1;
        LocalDate start = LocalDate.of(2020, 10, 17);
        List<LocalDate> firstRoundDates = getSaturdayDates(start, gamesInEachRound);

        String firstRoundGames = generateRound(teams, firstRoundDates, false);

        LocalDate firstRoundLastDate = firstRoundDates.get(firstRoundDates.size() - 1);
        LocalDate secondRoundStart = firstRoundLastDate.plusWeeks(3);
        List<LocalDate> secondRoundDates = getSaturdayDates(secondRoundStart, gamesInEachRound);

        String secondRoundGames = generateRound(teams, secondRoundDates, true);

        return firstRoundGames + "\n" + secondRoundGames;
    }

    private static List<LocalDate> getSaturdayDates(LocalDate startDate, int weeks) {
        List<LocalDate> dates = new ArrayList<>();
        for (int i = 0; i < weeks; i++) {
            dates.add(startDate);
            startDate = startDate.plusWeeks(1);
        }
        return dates;
    }

    private static String generateRound(List<TeamDto> teams,
                                        List<LocalDate> dates,
                                        boolean isReverse) {
        List<TeamDto> teamList1;
        List<TeamDto> teamList2;

        if (isReverse) {
            teamList1 = new ArrayList<>(teams.subList(teams.size() / 2, teams.size()));
            teamList2 = new ArrayList<>(teams.subList(0, teams.size() / 2));
        } else {
            teamList1 = new ArrayList<>(teams.subList(0, teams.size() / 2));
            teamList2 = new ArrayList<>(teams.subList(teams.size() / 2, teams.size()));
        }

        System.out.println(teamList1);
        System.out.println(teamList2);

        StringBuilder builder = new StringBuilder();
        for (LocalDate date : dates) {
            for (int i = 0; i < teams.size() / 2; i++) {
                builder.append(date).append(" 17:00; ")
                    .append(teamList1.get(i).name()).append("; ")
                    .append(teamList2.get(i).name()).append(";\n");
            }

            if (teamList2.size() != 1) {
                teamList2.add(0, teamList1.remove(1));
                teamList1.add(teamList2.remove(teamList2.size() - 1));
            }
        }

        return builder.toString();
    }

}
