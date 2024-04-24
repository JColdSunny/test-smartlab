package com.jcs.interview.controller;

import com.jcs.interview.dto.GameInfoDto;
import com.jcs.interview.dto.LeagueDto;
import com.jcs.interview.dto.RoundInfoDto;
import com.jcs.interview.dto.ScheduleInfoDto;
import com.jcs.interview.service.GameScheduleService;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.nio.file.Files;
import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({SoccerTeamController.class})
public class SoccerTeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameScheduleService gameScheduleService;

    @Test
    void testCreateScheduleReturnOk() throws Exception {
        File file = new ClassPathResource("soccer_teams_correct_request.json").getFile();

        when(gameScheduleService.generateGameSchedule(any(LeagueDto.class)))
                .thenReturn(new ScheduleInfoDto(List.of(
                        new RoundInfoDto(OffsetDateTime.now(),
                                List.of(new GameInfoDto(OffsetDateTime.now(), "team1", "team2")))
                )));

        mockMvc.perform(post("/soccer/teams/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new String(Files.readAllBytes(file.toPath()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rounds").isArray())
                .andExpect(jsonPath("$.rounds").isNotEmpty())
                .andExpect(jsonPath("$.rounds[0].round_start_date").isNotEmpty())
                .andExpect(jsonPath("$.rounds[0].games").isArray())
                .andExpect(jsonPath("$.rounds[0].games").isNotEmpty())
                .andExpect(jsonPath("$.rounds[0].games[0].date").isNotEmpty())
                .andExpect(jsonPath("$.rounds[0].games[0].first_team").isNotEmpty())
                .andExpect(jsonPath("$.rounds[0].games[0].second_team").isNotEmpty());
    }

    @Nested
    @Tag("bad request")
    class BadRequestTests {

        @Test
        void testCreateScheduleWhenEmptyTeamListReturnBadRequest() throws Exception {
            File file = new ClassPathResource("soccer_teams_when_no_teams.json").getFile();

            mockMvc.perform(post("/soccer/teams/schedule")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new String(Files.readAllBytes(file.toPath()))))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.massage").value("teams must not be empty"));
        }

        @Test
        void testCreateScheduleWhenLeagueIsBlank() throws Exception {
            File file = new ClassPathResource("soccer_teams_when_league_is_empty.json").getFile();

            mockMvc.perform(post("/soccer/teams/schedule")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new String(Files.readAllBytes(file.toPath()))))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.massage").value("league must not be null or blank"));
        }

        @Test
        void testCreateScheduleWhenCountryIsBlank() throws Exception {
            File file = new ClassPathResource("soccer_teams_when_country_is_empty.json").getFile();

            mockMvc.perform(post("/soccer/teams/schedule")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new String(Files.readAllBytes(file.toPath()))))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.massage").value("country must not be null or blank"));
        }

        @Test
        void testCreateScheduleWhenTeamNameIsBlank() throws Exception {
            File file = new ClassPathResource("soccer_teams_when_team_name_is_empty.json").getFile();

            mockMvc.perform(post("/soccer/teams/schedule")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new String(Files.readAllBytes(file.toPath()))))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.massage").value("name must not be null or blank"));
        }

        @Test
        void testCreateScheduleWhenTeamNumberIsOdd() throws Exception {
            File file = new ClassPathResource("soccer_teams_when_team_number_is_odd.json").getFile();

            when(gameScheduleService.generateGameSchedule(any(LeagueDto.class)))
                    .thenThrow(new ValidationException("test failed"));

            mockMvc.perform(post("/soccer/teams/schedule")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new String(Files.readAllBytes(file.toPath()))))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.massage").value("test failed"));
        }

    }
}