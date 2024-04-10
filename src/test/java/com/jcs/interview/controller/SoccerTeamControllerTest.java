package com.jcs.interview.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jcs.interview.dto.LeagueDto;
import com.jcs.interview.service.GameScheduleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({SoccerTeamController.class})
public class SoccerTeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameScheduleService gameScheduleService;

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
            .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateScheduleReturnOk() throws Exception {
        when(gameScheduleService.generate(any(LeagueDto.class)))
            .thenReturn("Schedule Created");

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
            .andExpect(content().string("Schedule Created"));
    }
}