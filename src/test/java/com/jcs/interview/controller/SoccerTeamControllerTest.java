package com.jcs.interview.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jcs.interview.service.SoccerTeamService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({SoccerTeamController.class})
public class SoccerTeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SoccerTeamService soccerTeamService;

    @Test
    void testCreateSchedule() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "schedule.csv",
            MediaType.MULTIPART_FORM_DATA_VALUE, "Content of the file".getBytes());
        when(soccerTeamService.prepareSchedule(file)).thenReturn("Schedule Created");

        mockMvc.perform(multipart("/soccer/teams/schedule").file(file))
            .andExpect(status().isOk())
            .andExpect(status().isOk())
            .andExpect(content().string("Schedule Created"));
    }
}