package com.jcs.interview.controller;

import com.jcs.interview.dto.GameInfoDto;
import com.jcs.interview.dto.LeagueDto;
import com.jcs.interview.dto.RoundInfoDto;
import com.jcs.interview.dto.ScheduleInfoDto;
import com.jcs.interview.service.GameScheduleService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
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
        when(gameScheduleService.generateGameSchedule(any(LeagueDto.class)))
                .thenReturn(new ScheduleInfoDto(List.of(
                        new RoundInfoDto(LocalDateTime.now(),
                                List.of(new GameInfoDto(LocalDateTime.now(), "team1", "team2")))
                )));

        mockMvc.perform(post("/soccer/teams/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "league": "smartlab_league",
                                  "country": "Germany",
                                  "teams": [
                                    {
                                      "name": "Best marketing",
                                      "founding_date": "1998"
                                    },
                                    {
                                      "name": "Hot operations",
                                      "founding_date": "2000"
                                    }
                                  ]
                                }
                                """))
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
            mockMvc.perform(post("/soccer/teams/schedule")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "league": "smartlab_league",
                                      "country": "Germany",
                                      "teams": []
                                    }
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.massage").value("teams must not be empty"));
        }

        @Test
        void testCreateScheduleWhenLeagueIsBlank() throws Exception {
            mockMvc.perform(post("/soccer/teams/schedule")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "league": "",
                                      "country": "Germany",
                                      "teams": [
                                        {
                                          "name": "Best marketing",
                                          "founding_date": "1998"
                                        },
                                        {
                                          "name": "Hot operations",
                                          "founding_date": "2000"
                                        }
                                      ]
                                    }
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.massage").value("league must not be null or blank"));
        }

        @Test
        void testCreateScheduleWhenCountryIsBlank() throws Exception {
            mockMvc.perform(post("/soccer/teams/schedule")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "league": "smartlab_league",
                                      "country": "",
                                      "teams": [
                                        {
                                          "name": "Best marketing",
                                          "founding_date": "1998"
                                        },
                                        {
                                          "name": "Hot operations",
                                          "founding_date": "2000"
                                        }
                                      ]
                                    }
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.massage").value("country must not be null or blank"));
        }

        @Test
        void testCreateScheduleWhenTeamNameIsBlank() throws Exception {
            mockMvc.perform(post("/soccer/teams/schedule")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "league": "smartlab_league",
                                      "country": "Germany",
                                      "teams": [
                                        {
                                          "name": "",
                                          "founding_date": "1998"
                                        },
                                        {
                                          "name": "Hot operations",
                                          "founding_date": "2000"
                                        }
                                      ]
                                    }
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.massage").value("name must not be null or blank"));
        }

    }
}